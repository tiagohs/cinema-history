#!/usr/bin/env python3
"""
Pipeline de tradução do conteúdo do Cinema History (assets/local).

Uso (a partir da raiz do repositório):
  python3 content-i18n/i18n.py status   --lang en
  python3 content-i18n/i18n.py tmdb                      # baixa/atualiza títulos oficiais (cache)
  python3 content-i18n/i18n.py export   --lang en --groups homecontent,maintopics [--max-chars 40000]
  python3 content-i18n/i18n.py import   --lang en content-i18n/work/en-batch.json
  python3 content-i18n/i18n.py apply    --lang en       # gera assets/local/en a partir do pt + traduções
  python3 content-i18n/i18n.py validate --lang en       # compara estrutura en x pt

Veja content-i18n/README.md.
"""
from __future__ import annotations

import argparse
import collections
import concurrent.futures
import copy
import json
import os
import re
import sys
import time
import urllib.parse
import urllib.request

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import i18n_lib as L  # noqa: E402

WORK_DIR = os.path.join(L.HERE, "work")
TMDB_LANG = {"pt": "pt-BR", "en": "en-US", "es": "es-MX"}
HREF_MOVIE_RE = re.compile(r"https://_\{[^}]*?'?id'?\s*:\s*(\d+)[^}]*?screen_type'?\s*:\s*'?movie", re.S)
HREF_PERSON_RE = re.compile(r"https://_\{[^}]*?'?id'?\s*:\s*(\d+)[^}]*?screen_type'?\s*:\s*'?person", re.S)


# --------------------------------------------------------------------------------------
# Coleta do conteúdo em português
# --------------------------------------------------------------------------------------

def collect_source():
    """Retorna (entries, specials). entries: id -> {src, tags, occurrences[], groups{}}."""
    entries: dict[str, dict] = {}
    specials: list[L.Special] = []
    for rel in L.iter_json_files(L.SOURCE_LANG):
        doc = L.load_json(os.path.join(L.lang_dir(L.SOURCE_LANG), rel))
        result = L.walk(doc, rel)
        specials.extend(result.specials)
        for occ, text in result.texts:
            masked, _ = L.mask(text)
            eid = L.entry_id(masked)
            e = entries.setdefault(eid, {"src": masked, "occurrences": [], "groups": set(), "keys": set()})
            e["occurrences"].append(occ)
            e["groups"].add(L.file_group(rel))
            e["keys"].add(occ.key)
    return entries, specials


# --------------------------------------------------------------------------------------
# status
# --------------------------------------------------------------------------------------

def cmd_status(args):
    entries, specials = collect_source()
    tr = L.load_translations(args.lang)
    per_group = collections.defaultdict(lambda: [0, 0, 0, 0])  # total, feitos, chars_total, chars_feitos
    stale = 0
    for eid, e in entries.items():
        done = eid in tr and tr[eid].get("src") == e["src"]
        if eid in tr and tr[eid].get("src") != e["src"]:
            stale += 1
        for g in e["groups"]:
            pg = per_group[g]
            pg[0] += 1
            pg[2] += len(e["src"])
            if done:
                pg[1] += 1
                pg[3] += len(e["src"])
    total = [sum(v[i] for v in per_group.values()) for i in range(4)]
    print(f"Idioma: {args.lang}   textos únicos: {len(entries)}   traduzidos: {sum(1 for k in entries if k in tr)}")
    print(f"{'grupo':<26}{'textos':>8}{'feitos':>8}{'caracteres':>12}{'%':>7}")
    for g in sorted(per_group):
        t, d, ct, cd = per_group[g]
        print(f"{g:<26}{t:>8}{d:>8}{ct:>12}{(100 * cd / ct if ct else 100):>6.1f}%")
    print(f"{'TOTAL (com repetição)':<26}{total[0]:>8}{total[1]:>8}{total[2]:>12}{(100 * total[3] / total[2] if total[2] else 100):>6.1f}%")
    cache = L.load_json(L.TMDB_CACHE) if os.path.exists(L.TMDB_CACHE) else {"movie": {}}
    ids = {s.tmdb_id for s in specials}
    print(f"Títulos de filmes via TMDB: {len(ids)} filmes, {len(ids & set(map(int, cache['movie'])))} em cache")
    if stale:
        print(f"ATENÇÃO: {stale} traduções desatualizadas (o texto em pt mudou).")


# --------------------------------------------------------------------------------------
# tmdb: títulos oficiais
# --------------------------------------------------------------------------------------

def _tmdb_key() -> str:
    key = os.environ.get("TMDB_API_KEY")
    if key:
        return key
    gradle = open(os.path.join(L.ROOT, "android", "app", "build.gradle"), encoding="utf-8").read()
    m = re.search(r'THEMOVIEDB_API_KEY",\s*\'"([0-9a-f]{32})"', gradle)
    if not m:
        sys.exit("Defina TMDB_API_KEY")
    return m.group(1)


def _fetch_movie(key: str, movie_id: int) -> dict | None:
    url = f"https://api.themoviedb.org/3/movie/{movie_id}?" + urllib.parse.urlencode(
        {"api_key": key, "language": "en-US", "append_to_response": "translations"})
    for attempt in range(4):
        try:
            with urllib.request.urlopen(url, timeout=30) as r:
                data = json.load(r)
            break
        except urllib.error.HTTPError as e:
            if e.code == 404:
                return None
            time.sleep(2 * (attempt + 1))
        except Exception:
            time.sleep(2 * (attempt + 1))
    else:
        return None

    original = data.get("original_title") or data.get("title")
    trans = (data.get("translations") or {}).get("translations") or []

    def find(lang, country=None):
        for t in trans:
            if t.get("iso_639_1") == lang and (country is None or t.get("iso_3166_1") == country):
                title = (t.get("data") or {}).get("title")
                if title:
                    return title
        return None

    return {
        "original": original,
        "year": (data.get("release_date") or "")[:4],
        "pt": find("pt", "BR") or find("pt") or original,
        "en": data.get("title") or find("en", "US") or original,
        "es": find("es", "MX") or find("es") or original,
    }


def _referenced_movie_ids(entries, specials) -> set[int]:
    ids = {s.tmdb_id for s in specials}
    # ids nos links internos <a href="https://_{... 'id': N, 'screen_type': 'movie'}">
    for rel in L.iter_json_files(L.SOURCE_LANG):
        raw = open(os.path.join(L.lang_dir(L.SOURCE_LANG), rel), encoding="utf-8").read()
        ids.update(int(x) for x in HREF_MOVIE_RE.findall(raw))
    return ids


def cmd_tmdb(args):
    entries, specials = collect_source()
    ids = _referenced_movie_ids(entries, specials)
    cache = L.load_json(L.TMDB_CACHE) if os.path.exists(L.TMDB_CACHE) else {"movie": {}}
    todo = sorted(i for i in ids if str(i) not in cache["movie"] or args.refresh)
    print(f"Filmes referenciados: {len(ids)}; a buscar: {len(todo)}")
    key = _tmdb_key()
    done = 0
    with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:
        for movie_id, info in zip(todo, pool.map(lambda i: _fetch_movie(key, i), todo)):
            if info:
                cache["movie"][str(movie_id)] = info
            done += 1
            if done % 200 == 0:
                print(f"  {done}/{len(todo)}")
    cache["movie"] = dict(sorted(cache["movie"].items(), key=lambda kv: int(kv[0])))
    L.dump_json(L.TMDB_CACHE, cache)
    missing = [i for i in ids if str(i) not in cache["movie"]]
    print(f"Cache: {len(cache['movie'])} filmes. Sem dados no TMDB: {len(missing)} {missing[:20]}")


# --------------------------------------------------------------------------------------
# export / import (lotes de tradução)
# --------------------------------------------------------------------------------------

def cmd_export(args):
    entries, _ = collect_source()
    tr = L.load_translations(args.lang)
    cache = L.load_json(L.TMDB_CACHE) if os.path.exists(L.TMDB_CACHE) else {"movie": {}}
    groups = set(args.groups.split(",")) if args.groups else None
    items, chars = [], 0
    # ordem estável: por grupo/arquivo/posição da primeira ocorrência
    ordered = sorted(entries.items(), key=lambda kv: (kv[1]["occurrences"][0].file, kv[1]["occurrences"][0].pointer))
    for eid, e in ordered:
        if eid in tr and tr[eid].get("src") == e["src"]:
            continue
        if groups and not (e["groups"] & groups):
            continue
        movies = {}
        for mid in HREF_MOVIE_RE.findall(_unmasked_example(e)):
            info = cache["movie"].get(mid)
            if info:
                movies[mid] = {"pt": info["pt"], args.lang: info[args.lang], "original": info["original"], "year": info["year"]}
        occ = e["occurrences"][0]
        item = {"id": eid, "src": e["src"], "where": f"{occ.file} {occ.pointer}", "keys": sorted(e["keys"])}
        if movies:
            item["movies"] = movies
        items.append(item)
        chars += len(e["src"])
        if args.max_chars and chars >= args.max_chars:
            break
    os.makedirs(WORK_DIR, exist_ok=True)
    out = args.out or os.path.join(WORK_DIR, f"{args.lang}-batch.json")
    L.dump_json(out, {"lang": args.lang, "items": items})
    print(f"Exportados {len(items)} textos ({chars} caracteres) -> {os.path.relpath(out, L.ROOT)}")


_SOURCE_RAW_CACHE: dict[str, object] = {}


def _unmasked_example(e) -> str:
    occ = e["occurrences"][0]
    doc = _SOURCE_RAW_CACHE.get(occ.file)
    if doc is None:
        doc = L.load_json(os.path.join(L.lang_dir(L.SOURCE_LANG), occ.file))
        _SOURCE_RAW_CACHE[occ.file] = doc
    container, key = L.resolve_pointer(doc, occ.pointer)
    return container[key]


def cmd_import(args):
    entries, _ = collect_source()
    tr = L.load_translations(args.lang)
    data = L.load_json(args.file)
    items = data["items"] if isinstance(data, dict) else data
    ok, rejected = 0, []
    for item in items:
        eid, text = item["id"], item.get("t")
        if text is None:
            continue
        e = entries.get(eid)
        if not e:
            rejected.append((eid, "id não existe mais no conteúdo pt"))
            continue
        errors = L.check_markers(e["src"], text)
        if errors:
            rejected.append((eid, "; ".join(errors)))
            continue
        tr[eid] = {"src": e["src"], "t": text}
        ok += 1
    L.save_translations(args.lang, tr)
    print(f"Importadas {ok} traduções para '{args.lang}'. Rejeitadas: {len(rejected)}")
    for eid, why in rejected[:50]:
        print(f"  {eid}: {why}")
    if rejected:
        sys.exit(1)


# --------------------------------------------------------------------------------------
# apply: gera assets/local/<lang>
# --------------------------------------------------------------------------------------

def build_language(lang: str):
    """Monta os documentos traduzidos em memória. Retorna (docs, relatório)."""
    tr = L.load_translations(lang)
    cache = L.load_json(L.TMDB_CACHE) if os.path.exists(L.TMDB_CACHE) else {"movie": {}}
    docs, missing, stale, titles_missing = {}, 0, 0, 0
    for rel in L.iter_json_files(L.SOURCE_LANG):
        src_doc = L.load_json(os.path.join(L.lang_dir(L.SOURCE_LANG), rel))
        doc = copy.deepcopy(src_doc)
        result = L.walk(src_doc, rel)
        for occ, text in result.texts:
            masked, tags = L.mask(text)
            eid = L.entry_id(masked)
            t = tr.get(eid)
            if not t:
                missing += 1
                continue
            if t.get("src") != masked:
                stale += 1
                continue
            container, key = L.resolve_pointer(doc, occ.pointer)
            container[key] = L.unmask(t["t"], tags)
        for sp in result.specials:
            info = cache["movie"].get(str(sp.tmdb_id))
            if not info or not info.get(lang):
                titles_missing += 1
                continue
            container, key = L.resolve_pointer(doc, sp.pointer)
            container[key] = info[lang]
        docs[rel] = doc
    return docs, {"missing": missing, "stale": stale, "titles_missing": titles_missing}


def cmd_apply(args):
    if args.lang == L.SOURCE_LANG:
        sys.exit("O português é a fonte; não é gerado.")
    docs, report = build_language(args.lang)
    if args.require_complete and (report["missing"] or report["stale"]):
        sys.exit(f"Tradução incompleta: {report}")
    out_dir = L.lang_dir(args.lang)
    for rel, doc in docs.items():
        L.dump_json(os.path.join(out_dir, rel), doc)
    # remove arquivos que não existem mais no pt
    for rel in list(L.iter_json_files(args.lang)):
        if rel not in docs:
            os.remove(os.path.join(out_dir, rel))
    print(f"Gerado assets/local/{args.lang}: {len(docs)} arquivos. "
          f"Textos ainda em pt: {report['missing']} | desatualizados: {report['stale']} | "
          f"títulos sem TMDB: {report['titles_missing']}")


# --------------------------------------------------------------------------------------
# validate: estrutura idêntica ao pt, exceto textos traduzíveis
# --------------------------------------------------------------------------------------

def cmd_validate(args):
    errors = []
    src_files = set(L.iter_json_files(L.SOURCE_LANG))
    dst_files = set(L.iter_json_files(args.lang)) if os.path.isdir(L.lang_dir(args.lang)) else set()
    for rel in sorted(src_files - dst_files):
        errors.append(f"{rel}: ausente em {args.lang}")
    for rel in sorted(dst_files - src_files):
        errors.append(f"{rel}: sobrando em {args.lang}")

    for rel in sorted(src_files & dst_files):
        src = L.load_json(os.path.join(L.lang_dir(L.SOURCE_LANG), rel))
        dst = L.load_json(os.path.join(L.lang_dir(args.lang), rel))
        allowed = set()
        walked = L.walk(src, rel)
        allowed.update(o.pointer for o, _ in walked.texts)
        allowed.update(s.pointer for s in walked.specials)
        _compare(src, dst, [], allowed, rel, errors)

    if errors:
        print(f"{len(errors)} problema(s) em '{args.lang}':")
        for e in errors[:200]:
            print("  " + e)
        sys.exit(1)
    print(f"OK: '{args.lang}' tem a mesma estrutura do pt ({len(src_files)} arquivos).")


def _hrefs(s: str) -> list[str]:
    return sorted(re.findall(r'href\s*=\s*"[^"]*"', s))


def _compare(a, b, parts, allowed, rel, errors):
    ptr = L._ptr(parts)
    if type(a) is not type(b):
        errors.append(f"{rel} {ptr}: tipo diferente")
        return
    if isinstance(a, dict):
        if a.keys() != b.keys():
            errors.append(f"{rel} {ptr}: chaves diferentes")
            return
        for k in a:
            _compare(a[k], b[k], parts + [k], allowed, rel, errors)
    elif isinstance(a, list):
        if len(a) != len(b):
            errors.append(f"{rel} {ptr}: tamanho da lista diferente")
            return
        for i, (x, y) in enumerate(zip(a, b)):
            _compare(x, y, parts + [i], allowed, rel, errors)
    elif isinstance(a, str) and ptr in allowed:
        if _hrefs(a) != _hrefs(b):
            errors.append(f"{rel} {ptr}: links (href) diferentes do pt")
        if a.count("\n") != b.count("\n"):
            errors.append(f"{rel} {ptr}: quebras de linha diferentes do pt")
    elif a != b:
        errors.append(f"{rel} {ptr}: valor não traduzível foi alterado ({str(a)[:40]!r} -> {str(b)[:40]!r})")


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = p.add_subparsers(dest="cmd", required=True)

    s = sub.add_parser("status"); s.add_argument("--lang", default="en"); s.set_defaults(fn=cmd_status)
    s = sub.add_parser("tmdb"); s.add_argument("--refresh", action="store_true"); s.set_defaults(fn=cmd_tmdb)
    s = sub.add_parser("export"); s.add_argument("--lang", required=True); s.add_argument("--groups")
    s.add_argument("--max-chars", type=int, default=0); s.add_argument("--out"); s.set_defaults(fn=cmd_export)
    s = sub.add_parser("import"); s.add_argument("--lang", required=True); s.add_argument("file"); s.set_defaults(fn=cmd_import)
    s = sub.add_parser("apply"); s.add_argument("--lang", required=True)
    s.add_argument("--require-complete", action="store_true"); s.set_defaults(fn=cmd_apply)
    s = sub.add_parser("validate"); s.add_argument("--lang", required=True); s.set_defaults(fn=cmd_validate)

    args = p.parse_args()
    args.fn(args)


if __name__ == "__main__":
    main()
