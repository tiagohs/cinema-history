#!/usr/bin/env python3
"""
Wikipédia + Wikidata para montar os prêmios com ids do TMDB exatos (sem busca por nome).

Uso como biblioteca (from wiki import ...):
    wikitext("1st Academy Awards")            -> wikitext da página (segue redirects; cache)
    links(wikitext_trecho)                    -> [(artigo, texto_exibido), ...] na ordem
    resolve(["Wings (1927 film)", "Emil Jannings"])
        -> {"Wings (1927 film)": {"qid": "Q...", "tmdb_movie": "28966", "tmdb_person": None, "label": "Wings"}, ...}
       (artigo -> item do Wikidata -> P4947 TMDB filme / P4985 TMDB pessoa; lotes de 50; cache)

Linha de comando:
    python3 content-src/wiki.py text "1st Academy Awards"      # imprime o wikitext
    python3 content-src/wiki.py resolve "Wings (1927 film)" "Emil Jannings"
"""
import hashlib
import json
import os
import re
import subprocess
import sys
import time
import urllib.parse

HERE = os.path.dirname(os.path.abspath(__file__))
CACHE = os.path.join(HERE, "cache", "wikidata")
UA = "CinemaHistoryContent/1.0 (https://github.com/tiagohs/cinema-history)"
os.makedirs(CACHE, exist_ok=True)


def _get(url, params):
    full = url + "?" + urllib.parse.urlencode(params)
    key = hashlib.sha1(full.encode()).hexdigest()
    path = os.path.join(CACHE, "h_" + key + ".json")
    if os.path.exists(path):
        return json.load(open(path))
    for attempt in range(6):
        out = subprocess.run(["curl", "-s", "-A", UA, "--max-time", "60", full], capture_output=True, text=True).stdout
        try:
            data = json.loads(out)
            if "error" in data and data["error"].get("code") in ("ratelimited", "maxlag"):
                raise ValueError
            json.dump(data, open(path, "w"))
            return data
        except Exception:
            time.sleep(3 * (attempt + 1))
    raise RuntimeError("sem resposta: " + full[:120])


def wikitext(page, lang="en"):
    d = _get(f"https://{lang}.wikipedia.org/w/api.php",
             {"action": "parse", "page": page, "prop": "wikitext", "redirects": 1, "format": "json", "formatversion": 2})
    if "error" in d:
        raise KeyError(f"{page}: {d['error'].get('info')}")
    return d["parse"]["wikitext"]


LINK = re.compile(r"\[\[([^\]|#]+)(?:#[^\]|]*)?(?:\|([^\]]*))?\]\]")


def links(text):
    return [(a.strip(), (b or a).strip()) for a, b in LINK.findall(text)
            if not re.match(r"(File|Image|Category|wikt|w):", a, re.I)]


def _norm(t):
    t = t.strip().replace("_", " ")
    return t[:1].upper() + t[1:]


def resolve(titles, lang="en"):
    titles = [_norm(t) for t in titles]
    out = {}
    todo = list(dict.fromkeys(titles))
    for i in range(0, len(todo), 50):
        chunk = todo[i:i + 50]
        d = _get(f"https://{lang}.wikipedia.org/w/api.php",
                 {"action": "query", "prop": "pageprops", "ppprop": "wikibase_item", "redirects": 1,
                  "titles": "|".join(chunk), "format": "json", "formatversion": 2})
        q = d.get("query", {})
        alias = {}
        for r in q.get("normalized", []) + q.get("redirects", []):
            alias[r["from"]] = r["to"]
        qid_of = {p["title"]: p.get("pageprops", {}).get("wikibase_item") for p in q.get("pages", [])}
        for t in chunk:
            final = t
            for _ in range(3):
                final = alias.get(final, final)
            out[t] = {"qid": qid_of.get(final), "page": final}
    qids = [v["qid"] for v in out.values() if v["qid"]]
    ents = {}
    for i in range(0, len(qids), 50):
        chunk = list(dict.fromkeys(qids[i:i + 50]))
        d = _get("https://www.wikidata.org/w/api.php",
                 {"action": "wbgetentities", "ids": "|".join(chunk), "props": "claims|labels",
                  "languages": "en", "format": "json"})
        ents.update(d.get("entities", {}))

    def claim(e, p):
        for c in e.get("claims", {}).get(p, []):
            v = c.get("mainsnak", {}).get("datavalue", {}).get("value")
            if v:
                return v
        return None

    for t, v in out.items():
        e = ents.get(v["qid"] or "", {})
        tm = claim(e, "P4947")
        tp = claim(e, "P4985")
        v.update({"tmdb_movie": tm, "tmdb_person": tp,
                  "label": e.get("labels", {}).get("en", {}).get("value")})
    return out


if __name__ == "__main__":
    if len(sys.argv) >= 3 and sys.argv[1] == "text":
        print(wikitext(sys.argv[2]))
    elif len(sys.argv) >= 3 and sys.argv[1] == "resolve":
        print(json.dumps(resolve(sys.argv[2:]), ensure_ascii=False, indent=1))
    else:
        print(__doc__)
