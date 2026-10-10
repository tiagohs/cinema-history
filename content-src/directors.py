#!/usr/bin/env python3
"""
Páginas especiais de diretores (Mestres das Telas) a partir do formato de autoria
content-src/pt/directors/<slug>.md. Gera, em assets/local/pt:

  - specials/persons.json      perfil especial de cada diretor (as entradas antigas, escritas à mão, são mantidas)
  - directorsmaintopics.json   lista da tela "Diretores" (cartões, épocas, regiões, "em alta")
  - references.json            categoria "Diretores" com as fontes (linhas "// fonte:")
  - res/drawable-xxhdpi        retrato de cada diretor (foto do TMDB, usada no cartão e no topo da página)

Formato (seções "# tipo"; linhas "chave: valor"; o corpo de "# perfil" é o próprio parágrafo):

    # diretor
    id: 5026                                (id da pessoa no TMDB)
    name: Akira Kurosawa
    custom_name: Akira<br/>Kurosawa          (nome no topo da página; <br/> quebra a linha)
    years: 1910–1998                         (ou "n. 1969" para quem está vivo)
    country: Japão
    era: silent | golden | new_waves | modern | contemporary
    region: europe | north_america | latin_america | asia | africa | oceania
    trending: 0                              (posição em "Em alta" — 1, 2, 3...; 0 = fora)
    tagline: frase curta do cartão (até ~70 caracteres)
    portrait: name=img_dir_kurosawa tmdb_profile=/arquivo.jpg   (sem tmdb_profile: foto principal do TMDB)
    portrait_description: Fotografia de Akira Kurosawa.
    quote: frase do diretor (cabeçalho da página)
    awards: resumo dos prêmios (opcional)

    # perfil
    years: 1910-1935
    Parágrafo com {{m:ID}} e {{p:ID}} (como nos capítulos).

    # imagem        image: tmdb_movie=ID | tmdb_path=/x.jpg ; title: ; text: ; source:
    # video         youtube: ID ; title: ; text: ; source:
    # citacao       quote: ; author:
    (cada mídia fica logo depois do parágrafo de perfil anterior)

    # videos        uma linha por vídeo da galeria: ID | título | canal | tipo (Entrevista, Trailer, Ensaio...)

    // fonte: Título — URL

Ritmo (GUIA.md): mídia entre o 1º e o 2º parágrafo; nunca mais de dois parágrafos sem mídia; nunca duas mídias
coladas; não repetir o mesmo tipo de mídia em sequência.

Comandos:
    directors.py check [arquivo.md ...]     valida e mostra o texto final (sem gravar)
    directors.py build                      grava os JSON e os retratos
"""
from __future__ import annotations

import glob
import io
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import build as B  # noqa: E402

SRC = os.path.join(B.HERE, "pt", "directors")
PERSONS = os.path.join(B.ASSETS, "specials", "persons.json")
TOPICS = os.path.join(B.ASSETS, "directorsmaintopics.json")
REFERENCES = os.path.join(B.ASSETS, "references.json")
REF_CATEGORY = "Diretores"

ERAS = ("silent", "golden", "new_waves", "modern", "contemporary")
REGIONS = ("europe", "north_america", "latin_america", "asia", "africa", "oceania")
MEDIA = ("imagem", "video", "citacao")
PORTRAIT_WIDTH = 540


def _check_video(vid, errors, notes, where):
    yt = B.youtube(vid)
    if "__error__" in yt:
        errors.append(f"[{where}] vídeo {vid} indisponível/sem incorporação ({yt['__error__']})")
    else:
        notes.append(f"vídeo {vid}: \"{yt.get('title')}\" — {yt.get('author_name')}")


def _online_image(url, description, height=220):
    return {"image_type": "online", "url": url, "content_description": description,
            "style": {"scale_type": "center_crop", "height": height}}


def _portrait_source(opts, pid):
    if "tmdb_profile" in opts:
        return "https://image.tmdb.org/t/p/h632" + opts["tmdb_profile"]
    if "url" in opts:
        return opts["url"]
    p = B.person(pid)
    path = p.get("profile_path")
    return "https://image.tmdb.org/t/p/h632" + path if path else None


def fetch_portrait(name, url, errors, write):
    out = os.path.join(B.DRAWABLE, name + ".webp")
    gen = B._generated
    existing = B.existing_drawables()
    if name in existing and gen.get(name) not in (None, url):
        errors.append(f"retrato {name} já existe no app com outra imagem")
        return
    if name in existing and name not in gen:
        errors.append(f"retrato {name} já existe no app: escolha outro nome")
        return
    if not write or not url or (os.path.exists(out) and gen.get(name) == url):
        return
    from PIL import Image
    import subprocess
    try:
        data = subprocess.run(["curl", "-sfL", "--max-time", "60", "-A", B.UA["User-Agent"], url],
                              check=True, capture_output=True).stdout
        im = Image.open(io.BytesIO(data)).convert("RGB")
        if im.width > PORTRAIT_WIDTH:
            im = im.resize((PORTRAIT_WIDTH, round(im.height * PORTRAIT_WIDTH / im.width)), Image.LANCZOS)
        im.save(out, "WEBP", quality=74, method=6)
        gen[name] = url
        B._save("images", gen)
    except Exception as e:  # noqa: BLE001
        errors.append(f"falha ao baixar retrato {name} ({url}): {e}")


def media_image_url(opts):
    if "tmdb_path" in opts:
        return "https://image.tmdb.org/t/p/w780" + opts["tmdb_path"]
    if "tmdb_movie" in opts:
        imgs = B.tmdb(f"/movie/{opts['tmdb_movie']}/images", include_image_language="null,en")
        backs = [b for b in (imgs.get("backdrops") or []) if b.get("iso_639_1") is None]
        backs.sort(key=lambda b: -(b.get("vote_average") or 0))
        if backs:
            return "https://image.tmdb.org/t/p/w780" + backs[0]["file_path"]
        m = B.movie(opts["tmdb_movie"])
        path = m.get("backdrop_path")
        return "https://image.tmdb.org/t/p/w780" + path if path else None
    return opts.get("url")


def parse_file(path):
    """Seções na ordem; '# perfil' guarda years + corpo; '# videos' guarda linhas; fontes à parte."""
    sections, cur, sources = [], None, []
    for raw in open(path, encoding="utf-8"):
        line = raw.rstrip("\n")
        m = re.match(r"^#\s+(\w+)\s*$", line)
        if m:
            cur = {"type": m.group(1).lower(), "fields": {}, "body": []}
            sections.append(cur)
            continue
        s = line.strip()
        fm = re.match(r"^//\s*fonte:\s*(.+?)\s+[—-]\s+(https?://\S+)\s*$", s)
        if fm:
            sources.append((fm.group(1).strip(), fm.group(2).strip()))
            continue
        if cur is None or not s or s.startswith("//"):
            continue
        if cur["type"] == "videos":
            cur["body"].append(s)
            continue
        kv = re.match(r"^(\w+):\s?(.*)$", s)
        if kv and not (cur["type"] == "perfil" and kv.group(1).lower() != "years"):
            cur["fields"][kv.group(1).lower()] = kv.group(2).strip()
            cur["last"] = kv.group(1).lower()
        elif cur["type"] == "perfil":
            cur["body"].append(s)
        elif cur.get("last"):
            cur["fields"][cur["last"]] += " " + s
    return sections, sources


def director(path, write=False):
    errors, notes = [], []
    secs, sources = parse_file(path)
    head = next((s["fields"] for s in secs if s["type"] == "diretor"), None)
    if head is None:
        return {"errors": ["falta # diretor"], "notes": [], "path": path}

    for k in ("id", "name", "custom_name", "years", "country", "era", "region", "tagline", "portrait", "quote"):
        if not head.get(k):
            errors.append(f"[diretor] falta '{k}'")
    pid = int(head.get("id", "0") or 0)
    if head.get("era") not in ERAS:
        errors.append(f"[diretor] era inválida: {head.get('era')}")
    if head.get("region") not in REGIONS:
        errors.append(f"[diretor] region inválida: {head.get('region')}")
    p = B.person(pid)
    if "__error__" in p:
        errors.append(f"pessoa {pid} não existe no TMDB")
    elif p.get("name") and B.plain(head.get("name", "")).lower() != p["name"].lower():
        notes.append(f"nome no TMDB: {p['name']}")

    popts = dict(kv.split("=", 1) for kv in head.get("portrait", "").split() if "=" in kv)
    pname = popts.get("name", "")
    if not pname.startswith("img_dir_"):
        errors.append("portrait sem name=img_dir_*")
    purl = _portrait_source(popts, pid)
    if not purl:
        errors.append("diretor sem foto no TMDB: use tmdb_profile= ou url=")
    fetch_portrait(pname, purl, errors, write)

    profile, sequence, videos = [], [], []
    last_media = None
    for s in secs:
        t, f = s["type"], s["fields"]
        if t == "perfil":
            if not f.get("years") or not s["body"]:
                errors.append("[perfil] precisa de 'years:' e de um parágrafo")
            text = B.render(" ".join(s["body"]), errors)
            words = len(B.plain(text).split())
            if words < 45 or words > 190:
                notes.append(f"[perfil {f.get('years')}] {words} palavras (ideal 70–160)")
            profile.append({"years": f.get("years", ""), "content": text})
            sequence.append("p")
        elif t in MEDIA:
            if not profile:
                errors.append(f"[{t}] mídia antes do primeiro parágrafo")
                continue
            if "media" in profile[-1]:
                errors.append(f"[{t}] duas mídias coladas depois de '{profile[-1]['years']}'")
                continue
            if last_media == t:
                errors.append(f"[{t}] mesmo tipo de mídia em sequência")
            last_media = t
            sequence.append("m")
            if t == "imagem":
                for k in ("image", "title", "text", "source"):
                    if not f.get(k):
                        errors.append(f"[imagem] falta '{k}'")
                iopts = dict(kv.split("=", 1) for kv in f.get("image", "").split() if "=" in kv)
                url = media_image_url(iopts)
                if not url:
                    errors.append(f"[imagem] sem imagem para {f.get('image')}")
                media = {"type": "image", "image": _online_image(url, B.plain(B.render(f.get("title", ""), errors))),
                         "title": B.plain(B.render(f.get("title", ""), errors)), "text": B.render(f.get("text", ""), errors),
                         "source": f.get("source", "")}
                notes.append(f"imagem: {url}")
            elif t == "video":
                for k in ("youtube", "title", "text", "source"):
                    if not f.get(k):
                        errors.append(f"[video] falta '{k}'")
                _check_video(f.get("youtube", ""), errors, notes, "video")
                yt = B.youtube(f.get("youtube", ""))
                source = f.get("source", "")
                if source == "*":
                    source = f"Youtube: Canal <i>{yt.get('author_name', '')}</i>"
                media = {"type": "video", "video_id": f.get("youtube"), "title": B.plain(B.render(f.get("title", ""), errors)),
                         "text": B.render(f.get("text", ""), errors), "source": source}
            else:
                for k in ("quote", "author"):
                    if not f.get(k):
                        errors.append(f"[citacao] falta '{k}'")
                media = {"type": "quote", "quote": f.get("quote", ""), "author": f.get("author", "")}
            profile[-1]["media"] = media
        elif t == "videos":
            for line in s["body"]:
                parts = [x.strip() for x in line.split("|")]
                if len(parts) != 4:
                    errors.append(f"[videos] linha inválida: {line}")
                    continue
                _check_video(parts[0], errors, notes, "videos")
                yt = B.youtube(parts[0])
                name = yt.get("title", "") if parts[1] == "*" else parts[1]
                source = yt.get("author_name", "") if parts[2] == "*" else parts[2]
                videos.append({"name": name, "source": source, "type": parts[3], "key": parts[0]})
        elif t != "diretor":
            errors.append(f"seção desconhecida: # {t}")

    # Ritmo do GUIA: mídia entre o 1º e o 2º parágrafo; no máximo dois parágrafos seguidos sem mídia.
    if len(profile) < 3:
        errors.append("perfil com menos de 3 parágrafos")
    if len(profile) > 1 and "media" not in profile[0]:
        errors.append("falta mídia entre o 1º e o 2º parágrafo do perfil")
    run = 0
    for item in sequence:
        run = run + 1 if item == "p" else 0
        if run > 2:
            errors.append("mais de dois parágrafos seguidos sem imagem, vídeo ou citação")
            break
    if not videos:
        errors.append("falta # videos (galeria)")
    inline = {pr["media"].get("video_id") for pr in profile if pr.get("media", {}).get("type") == "video"}
    if inline & {v["key"] for v in videos}:
        errors.append("vídeo repetido entre o perfil e a galeria")
    if len(sources) < 2:
        errors.append("menos de duas fontes (// fonte: Título — URL)")

    special = {"id": pid, "name": head.get("name"), "custom_name": head.get("custom_name"),
               "highlight_image": pname, "quote": head.get("quote"), "profile": profile}
    if head.get("awards"):
        special["awards"] = head["awards"]
    special["videos"] = videos

    topic = {"main_topic_type": "directors", "layout_type": "card_full", "person_id": pid, "title": head.get("name"),
             "years": head.get("years"), "country": head.get("country"), "description": head.get("tagline"),
             "era": head.get("era"), "region": head.get("region"), "trending": int(head.get("trending") or 0),
             "image": {"image_type": "local", "url": pname,
                       "content_description": head.get("portrait_description") or f"Fotografia de {head.get('name')}.",
                       "style": {"height": 350, "resize": {"height": 350}, "scale_type": "center_crop"}}}
    birth = (p.get("birthday") or "9999")[:4] if isinstance(p, dict) else "9999"
    return {"path": path, "special": special, "topic": topic, "sources": sources, "errors": errors, "notes": notes,
            "birth": birth}


# Metadados de lista dos diretores que já existiam (páginas escritas à mão em persons.json).
EXISTING = os.path.join(SRC, "_existentes.json")


def report(d):
    sp = d.get("special") or {}
    print(f"=== {os.path.basename(d['path'])}: {sp.get('name', '?')}")
    for pr in sp.get("profile", []):
        print(f"  [{pr['years']}] {B.plain(pr['content'])[:160]}…")
        if "media" in pr:
            m = pr["media"]
            print(f"     -> {m['type']}: {m.get('title') or m.get('quote', '')[:80]}")
    for n in d["notes"]:
        print("  nota:", n)
    for e in d["errors"]:
        print("  ERRO:", e)


def all_files(args):
    return args or sorted(f for f in glob.glob(os.path.join(SRC, "*.md")))


def cmd_check(args):
    total = 0
    for f in all_files(args):
        d = director(f, write=False)
        report(d)
        total += len(d["errors"])
    print(f"\n{total} erro(s)")
    return total


def cmd_build(_args):
    results = [director(f, write=True) for f in all_files([])]
    bad = [d for d in results if d["errors"]]
    for d in bad:
        report(d)
    if bad:
        sys.exit("Corrija os erros antes de gravar.")
    ids = {d["special"]["id"] for d in results}

    persons = json.load(open(PERSONS, encoding="utf-8"))
    kept = [p for p in persons if p["id"] not in ids]
    by_id = {d["special"]["id"]: d["special"] for d in results}
    persons_out = kept + [by_id[i] for i in sorted(by_id, key=lambda i: next(d["birth"] for d in results if d["special"]["id"] == i))]
    _dump(PERSONS, persons_out)

    existing = json.load(open(EXISTING, encoding="utf-8"))
    old_topics = {t["person_id"]: t for t in json.load(open(TOPICS, encoding="utf-8"))}
    topics = []
    for e in existing:
        t = dict(old_topics.get(e["person_id"]) or {})
        if not t:
            continue
        t.update({k: e[k] for k in ("years", "country", "description", "era", "region", "trending") if k in e})
        topics.append((e.get("birth", "9999"), t))
    topics += [(d["birth"], d["topic"]) for d in results]
    topics.sort(key=lambda bt: (ERAS.index(bt[1]["era"]), bt[0]))
    _dump(TOPICS, [t for _, t in topics])

    refs = json.load(open(REFERENCES, encoding="utf-8"))
    refs = [r for r in refs if r.get("name") != REF_CATEGORY]
    seen, items = set(), []
    for d in sorted(results, key=lambda d: d["special"]["name"]):
        for title, url in d["sources"]:
            if url in seen:
                continue
            seen.add(url)
            items.append({"type": "text", "text": f"{title}. Disponível em: <a href=\"https://_{{'type': 'online', "
                                                  f"'id': 11523, 'url': '{url}'}}\">{url}</a>."})
    refs.append({"name": REF_CATEGORY, "references": items})
    _dump(REFERENCES, refs)
    print(f"{len(results)} diretores gravados; {len(topics)} na lista; {len(items)} referências.")


def _dump(path, data):
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(data, fh, ensure_ascii=False, indent=2)
        fh.write("\n")


def main():
    if len(sys.argv) < 2 or sys.argv[1] not in ("check", "build"):
        print(__doc__)
        return
    if sys.argv[1] == "check":
        sys.exit(1 if cmd_check(sys.argv[2:]) else 0)
    cmd_build(sys.argv[2:])


if __name__ == "__main__":
    main()
