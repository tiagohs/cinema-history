#!/usr/bin/env python3
"""
Gera os anos antigos dos prêmios (vencedores e indicados) a partir do Wikidata, que traz os ids do TMDB.

Configuração: content-src/pt/awards/<id>/categorias.json
    {
      "year_from": 1929, "year_to": 2020,
      "year_source": "ceremony",          # "ceremony": data da cerimônia (P805 -> P585, senão P585)
                                          # "point_in_time_plus_1": P585 é o ano dos filmes; cerimônia = +1
      "categories": [
        {"name": "Melhor Filme", "qid": "Q102427", "kind": "movie"},
        {"name": "Melhor Ator", "qid": "Q103916", "kind": "person"},
        {"name": "Melhor Atuação", "qid": ["Q1", "Q2"], "kind": "person", "from": 2023}  # vários QIDs / faixa
      ]
    }

    python3 content-src/awards_wikidata.py fetch <id>          # baixa (cache em content-src/cache/wikidata/)
    python3 content-src/awards_wikidata.py report <id>         # resumo por ano/categoria (sem vencedor, poucos indicados)
    python3 content-src/awards_wikidata.py write <id> [--force] [--years 1929-1950]
        # escreve content-src/pt/awards/<id>/<ano>.md para os anos que ainda não têm .md
        # (e que não estão no JSON do app, a não ser com --force). Depois: awards.py check/build.

Regras do texto gerado:
- vencedor = item com "award received" (P166) daquela categoria no ano; indicados = "nominated for" (P1411).
- pessoa: p:<tmdb pessoa> @ m:<tmdb do filme em "for work" (P1686)>; sem TMDB: x: Nome | Filme.
- categoria só com o vencedor quando o Wikidata não tem indicados daquele ano.
"""
import hashlib
import json
import os
import re
import subprocess
import sys
import time
import urllib.parse
from collections import defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
SRC = os.path.join(HERE, "pt", "awards")
CACHE = os.path.join(HERE, "cache", "wikidata")
APP = os.path.join(ROOT, "android/app/src/main/assets/local/pt/awards/nominees")
UA = "CinemaHistoryContent/1.0 (https://github.com/tiagohs/cinema-history)"
os.makedirs(CACHE, exist_ok=True)

QUERY = """
SELECT ?item ?itemLabel ?kind ?date ?cerDate ?work ?workLabel ?tmdbM ?tmdbP ?workTmdb WHERE {
  VALUES (?p ?ps ?kind) { (p:P166 ps:P166 "win") (p:P1411 ps:P1411 "nom") }
  ?item ?p ?st . ?st ?ps wd:%s .
  OPTIONAL { ?st pq:P585 ?date }
  OPTIONAL { ?st pq:P805 ?cer . ?cer wdt:P585 ?cerDate }
  OPTIONAL { ?st pq:P1686 ?work . OPTIONAL { ?work wdt:P4947 ?workTmdb } }
  OPTIONAL { ?item wdt:P4947 ?tmdbM }
  OPTIONAL { ?item wdt:P4985 ?tmdbP }
  SERVICE wikibase:label { bd:serviceParam wikibase:language "en,pt,fr,es,de,it". }
}
"""


def sparql(q):
    key = hashlib.sha1(q.encode()).hexdigest()
    path = os.path.join(CACHE, key + ".json")
    if os.path.exists(path):
        return json.load(open(path))
    for attempt in range(6):
        out = subprocess.run(["curl", "-s", "-G", "-A", UA, "-H", "Accept: application/sparql-results+json",
                              "--max-time", "90", "--data-urlencode", "query=" + q,
                              "https://query.wikidata.org/sparql"], capture_output=True, text=True).stdout
        try:
            data = json.loads(out)["results"]["bindings"]
            json.dump(data, open(path, "w"))
            return data
        except Exception:
            time.sleep(5 * (attempt + 1))
    raise RuntimeError("Wikidata não respondeu: " + q[:80])


def val(b, k):
    v = b.get(k, {}).get("value")
    if v and v.startswith("http://www.wikidata.org/entity/"):
        return v.rsplit("/", 1)[1]
    return v


def config(aid):
    return json.load(open(os.path.join(SRC, str(aid), "categorias.json")))


def qids(cat):
    return cat["qid"] if isinstance(cat["qid"], list) else [cat["qid"]]


def year_of(b, mode):
    d = val(b, "cerDate") if mode == "ceremony" else None
    d = d or val(b, "date")
    if not d:
        return None
    y = int(d[:4])
    if mode == "point_in_time_plus_1":
        y += 1
    return y


def collect(aid):
    """-> {ano: {categoria: {chave: entrada}}}"""
    cfg = config(aid)
    mode = cfg.get("year_source", "ceremony")
    data = defaultdict(lambda: defaultdict(dict))
    for cat in cfg["categories"]:
        for qid in qids(cat):
            for b in sparql(QUERY % qid):
                y = year_of(b, mode)
                if y is None or not (cat.get("from", cfg["year_from"]) <= y <= cat.get("to", cfg["year_to"])):
                    continue
                item = val(b, "item")
                key = (item, val(b, "work"))
                e = data[y][cat["name"]].setdefault(key, {
                    "label": val(b, "itemLabel"), "tmdbM": val(b, "tmdbM"), "tmdbP": val(b, "tmdbP"),
                    "work": val(b, "workLabel"), "workTmdb": val(b, "workTmdb"), "win": False, "qid": item})
                if val(b, "kind") == "win":
                    e["win"] = True
                for k in ("tmdbM", "tmdbP", "workTmdb", "work"):
                    e[k] = e[k] or val(b, k if k != "work" else "workLabel")
    # pessoa vencedora sem "for work" + a mesma pessoa indicada com o filme: junta
    for y in data:
        for cname, entries in data[y].items():
            for (item, work), e in list(entries.items()):
                if work is None and e["win"]:
                    twins = [k for k in entries if k[0] == item and k[1] is not None]
                    if len(twins) == 1:
                        entries[twins[0]]["win"] = True
                        del entries[(item, work)]
                elif work is None and any(k[0] == item and k[1] is not None for k in entries):
                    del entries[(item, work)]
    return cfg, data


def line(cat, e):
    mark = "*" if e["win"] else "-"
    clean = lambda s: (s or "?").replace("|", "/").replace("[", "(").replace("]", ")").replace('"', "'")
    if cat["kind"] == "person":
        if e["tmdbP"]:
            mov = f" @ m:{e['workTmdb']}" if e["workTmdb"] else ""
            extra = f" [{clean(e['work'])}]" if e["work"] and not e["workTmdb"] else ""
            return f"{mark} p:{e['tmdbP']}{mov}{extra}"
        return f"{mark} x: {clean(e['label'])}" + (f" | {clean(e['work'])}" if e["work"] else "")
    # categoria de filme: o item pode ser o filme ou a pessoa (com o filme em "for work")
    if e["tmdbM"]:
        return f"{mark} m:{e['tmdbM']}"
    if e["workTmdb"]:
        return f"{mark} m:{e['workTmdb']} [{clean(e['label'])}]"
    return f"{mark} x: {clean(e['work'] or e['label'])}"


def existing_years(aid):
    path = os.path.join(APP, f"nominees_{aid}.json")
    years = set()
    if os.path.exists(path):
        years |= {int(y["year"]) for y in json.load(open(path))}
    d = os.path.join(APP, str(aid))
    if os.path.isdir(d):
        years |= {int(f[:4]) for f in os.listdir(d) if re.match(r"\d{4}\.json$", f)}
    return years


def cmd_fetch(aid):
    cfg = config(aid)
    for cat in cfg["categories"]:
        for qid in qids(cat):
            n = len(sparql(QUERY % qid))
            print(f"{cat['name']:<45} {qid:<12} {n} registros")


def cmd_report(aid):
    cfg, data = collect(aid)
    for y in range(cfg["year_from"], cfg["year_to"] + 1):
        if y not in data:
            print(f"{y}: (nada)")
            continue
        parts = []
        for cat in cfg["categories"]:
            es = data[y].get(cat["name"], {})
            if not es:
                continue
            w = sum(1 for e in es.values() if e["win"])
            no_tmdb = sum(1 for e in es.values() if not (e["tmdbM"] or e["tmdbP"] or e["workTmdb"]))
            flag = "" if w >= 1 else " SEM-VENCEDOR"
            parts.append(f"{cat['name']}={w}v/{len(es)}" + (f"/{no_tmdb}x" if no_tmdb else "") + flag)
        print(f"{y}: " + "; ".join(parts))


def cmd_write(aid, force, years):
    cfg, data = collect(aid)
    have = existing_years(aid)
    written = 0
    for y in sorted(data):
        if years and not (years[0] <= y <= years[1]):
            continue
        path = os.path.join(SRC, str(aid), f"{y}.md")
        if os.path.exists(path) and not force:
            continue
        if y in have and not force:
            continue
        out = [f"year: {y}", "<!-- gerado por awards_wikidata.py a partir do Wikidata; revise e corrija à mão -->"]
        for cat in cfg["categories"]:
            es = list(data[y].get(cat["name"], {}).values())
            if not es or not any(e["win"] for e in es):
                continue
            es.sort(key=lambda e: (not e["win"], e["label"] or ""))
            out.append(f"## cat: {cat['name']}")
            out += [line(cat, e) for e in es]
        if len(out) > 2:
            open(path, "w").write("\n".join(out) + "\n")
            written += 1
    print(f"{written} anos escritos em {os.path.join(SRC, str(aid))}")


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        return 1
    cmd, aid = sys.argv[1], int(sys.argv[2])
    years = None
    if "--years" in sys.argv:
        a, b = sys.argv[sys.argv.index("--years") + 1].split("-")
        years = (int(a), int(b))
    {"fetch": lambda: cmd_fetch(aid), "report": lambda: cmd_report(aid),
     "write": lambda: cmd_write(aid, "--force" in sys.argv, years)}[cmd]()
    return 0


if __name__ == "__main__":
    sys.exit(main())
