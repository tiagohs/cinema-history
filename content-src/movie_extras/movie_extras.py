#!/usr/bin/env python3
"""
Seções extras da tela de filme ("Você sabia?", citação, críticas, onde assistir no YouTube)
para os filmes da lista 1001 filmes e dos blocos "Vale a conferida…" dos capítulos.

Os dados extras ficam em android/app/src/main/assets/local/pt/specials/movies.json
(casados pelo id do TMDB). Este script reúne as fontes, verifica links e aplica no JSON.

Uso (na raiz do repositório):
  python3 content-src/movie_extras/movie_extras.py index      # ids + TMDB + Wikidata  -> index.json
  python3 content-src/movie_extras/movie_extras.py reviews [--ids 1,2] [--refresh]
        # críticas: pt (Plano Crítico), en (Rotten Tomatoes -> veículos), es (El Antepenúltimo
        # Mohicano, Encadenados); só entram URLs que respondem 200 (ou com cópia no Wayback
        # Machine quando o site bloqueia robôs)  -> reviews.json
  python3 content-src/movie_extras/movie_extras.py youtube    # filme completo no YouTube (domínio público
        # nos EUA ou canal oficial), conferido por oEmbed e duração  -> youtube.json
  python3 content-src/movie_extras/movie_extras.py wiki --ids 1,2,3   # trechos da Wikipédia (en/pt) para
        # quem escreve as curiosidades (impressos na tela)
  python3 content-src/movie_extras/movie_extras.py todo [--n 20]  # próximos filmes sem curiosidades
  python3 content-src/movie_extras/movie_extras.py apply      # aplica tudo no pt/specials/movies.json e
        # grava as traduções (en/es) de curiosidades/citações no catálogo do content-i18n

Curiosidades e citações são escritas à mão em curated/*.json (veja GUIA_EXTRAS.md).
"""
from __future__ import annotations

import argparse
import concurrent.futures
import glob
import html
import json
import os
import re
import subprocess
import sys
import time
import urllib.parse

HERE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.dirname(HERE)
ROOT = os.path.dirname(SRC)
sys.path.insert(0, SRC)
import build as B  # noqa: E402

ASSETS = os.path.join(ROOT, "android/app/src/main/assets/local/pt")
INDEX = os.path.join(HERE, "index.json")
REVIEWS = os.path.join(HERE, "reviews.json")
YOUTUBE = os.path.join(HERE, "youtube.json")
CURATED = os.path.join(HERE, "curated")
CACHE = os.path.join(HERE, "cache")
os.makedirs(CACHE, exist_ok=True)
BROWSER_UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126 Safari/537.36"
BOT_UA = "CinemaHistoryContent/1.0 (https://github.com/tiagohs/cinema-history)"


def load(path, default=None):
    return json.load(open(path)) if os.path.exists(path) else default


def save(path, data):
    tmp = path + ".tmp"
    json.dump(data, open(tmp, "w"), ensure_ascii=False, indent=1, sort_keys=True)
    os.replace(tmp, path)


CACHE_ONLY = False


def curl(url, ua=BROWSER_UA, timeout=30, extra=None):
    """(status, body) com cache em disco (só respostas 200/404)."""
    import hashlib
    key = hashlib.sha1(url.encode()).hexdigest()
    path = os.path.join(CACHE, key[:2], key + ".txt")
    if os.path.exists(path):
        raw = open(path, encoding="utf-8", errors="replace").read()
        code, _, body = raw.partition("\n")
        return int(code), body
    if CACHE_ONLY:
        return 0, ""
    args = ["curl", "-s", "-L", "-A", ua, "--max-time", str(timeout), "-w", "\n__HTTP__%{http_code}", url]
    if extra:
        args[1:1] = extra
    wiki = re.search(r"wiki(pedia|data|quote)\.org", url)
    for attempt in range(6 if wiki else 1):
        out = subprocess.run(args, capture_output=True).stdout.decode("utf-8", "replace")
        body, _, code = out.rpartition("\n__HTTP__")
        try:
            code = int(code)
        except ValueError:
            code = 0
        if code != 429:
            break
        time.sleep(10 * (attempt + 1))
    if code in (200, 404, 410):
        os.makedirs(os.path.dirname(path), exist_ok=True)
        # Páginas HTML grandes (críticas, Rotten Tomatoes) ocupam muito disco: guarda só o necessário
        # (páginas muito grandes não vão para o cache: buscas do YouTube têm ~1 MB cada)
        if len(body) <= 400_000:
            try:
                open(path, "w", encoding="utf-8").write(f"{code}\n{body}")
            except OSError:
                pass
    return code, body


# --------------------------------------------------------------------------------------
# Índice
# --------------------------------------------------------------------------------------

def collect_ids():
    spec = json.load(open(os.path.join(ASSETS, "specials", "movies.json")))
    mil = {}
    for g in spec:
        for m in g["movies"]:
            mil[m["id"]] = g["milMoviesMainTopicID"]
    vac = {}

    def walk(o, era):
        if isinstance(o, dict):
            if o.get("type") == "movie_list":
                for m in o.get("movies", []):
                    vac.setdefault(m["id"], era)
            for v in o.values():
                walk(v, era)
        elif isinstance(o, list):
            for v in o:
                walk(v, era)

    for f in sorted(glob.glob(os.path.join(ASSETS, "pages", "main_*", "*.json"))):
        era = int(re.search(r"main_(\d+)", f).group(1))
        walk(json.load(open(f)), era)
    return mil, vac


def sparql(query):
    code, body = curl("https://query.wikidata.org/sparql?" + urllib.parse.urlencode({"query": query, "format": "json"}),
                      ua=BOT_UA, timeout=90)
    if code != 200:
        raise RuntimeError(f"sparql {code}")
    return json.loads(body)["results"]["bindings"]


def cmd_index(args):
    mil, vac = collect_ids()
    ids = sorted(set(mil) | set(vac))
    print(f"{len(ids)} filmes ({len(mil)} da lista 1001, {len(vac)} em 'Vale a conferida')")
    index = load(INDEX, {})

    def fetch(mid):
        d = B.tmdb(f"/movie/{mid}", language="pt-BR", append_to_response="credits,translations,external_ids")
        return mid, d

    if True:  # o cache SQLite do build.py não é compartilhável entre threads
        for mid, d in map(fetch, ids):
            if "__error__" in d:
                print("  TMDB erro", mid, d)
                continue
            tr = {}
            for t in (d.get("translations") or {}).get("translations", []):
                k = f"{t['iso_639_1']}-{t['iso_3166_1']}"
                if t["data"].get("title"):
                    tr[k] = t["data"]["title"]
            e = index.setdefault(str(mid), {})
            e.update({
                "id": mid,
                "title_pt": d.get("title"),
                "title_en": tr.get("en-US") or d.get("original_title"),
                "title_es": tr.get("es-ES") or tr.get("es-MX") or d.get("original_title"),
                "original_title": d.get("original_title"),
                "year": int((d.get("release_date") or "0")[:4] or 0),
                "release_date": d.get("release_date"),
                "runtime": d.get("runtime"),
                "imdb": d.get("imdb_id"),
                "lang": d.get("original_language"),
                "countries": [c["iso_3166_1"] for c in d.get("production_countries", [])],
                "directors": [c["name"] for c in (d.get("credits") or {}).get("crew", []) if c.get("job") == "Director"],
                "mil": mil.get(mid),
                "vac": vac.get(mid),
            })
    # Wikidata: artigos da Wikipédia e id do Rotten Tomatoes
    for i in range(0, len(ids), 150):
        chunk = ids[i:i + 150]
        q = ("SELECT ?item ?tmdb ?rt ?en ?pt ?es ?wq WHERE { VALUES ?tmdb {" + " ".join(f'"{x}"' for x in chunk) +
             "} ?item wdt:P4947 ?tmdb . OPTIONAL{?item wdt:P1258 ?rt} "
             "OPTIONAL{?en schema:about ?item; schema:isPartOf <https://en.wikipedia.org/>} "
             "OPTIONAL{?pt schema:about ?item; schema:isPartOf <https://pt.wikipedia.org/>} "
             "OPTIONAL{?es schema:about ?item; schema:isPartOf <https://es.wikipedia.org/>} "
             "OPTIONAL{?wq schema:about ?item; schema:isPartOf <https://en.wikiquote.org/>} }")
        for b in sparql(q):
            e = index.get(b["tmdb"]["value"])
            if not e:
                continue
            e["qid"] = b["item"]["value"].rsplit("/", 1)[1]
            for k in ("rt", "en", "pt", "es", "wq"):
                if k in b and not e.get(k):
                    e[k] = b[k]["value"]
        print(f"  wikidata {i + len(chunk)}/{len(ids)}")
    save(INDEX, index)
    print("ok", INDEX)


def ordered_ids(index):
    """Prioridade: 'Vale a conferida' primeiro, depois 1001 filmes; dentro de cada grupo, do mais antigo."""
    return [e["id"] for e in sorted(index.values(), key=lambda e: (0 if e.get("vac") else 1, e.get("year") or 9999, e["id"]))]


# --------------------------------------------------------------------------------------
# Críticas
# --------------------------------------------------------------------------------------

def norm(s):
    import unicodedata
    s = unicodedata.normalize("NFKD", html.unescape(s or "")).encode("ascii", "ignore").decode().lower()
    return re.sub(r"[^a-z0-9]+", " ", s).strip()


def strip_tags(s):
    s = re.sub(r"<(script|style)[^>]*>.*?</\1>", " ", s or "", flags=re.S)
    s = re.sub(r"<br\s*/?>|</p>|</div>|</h\d>", "\n", s)
    s = re.sub(r"<[^>]+>", " ", s)
    s = html.unescape(s)
    return re.sub(r"[ \t\xa0]+", " ", s)


SENT_SPLIT = re.compile(r"(?<=[.!?…])(?<!\b[A-Z]\.)(?<!\bSt\.)(?<!\bDr\.)(?<!\bMr\.)\s+")


def words(s):
    return len(s.split())


def shorten(text, limit=25):
    """Trecho de no máximo `limit` palavras, cortado em fim de frase quando possível."""
    text = re.sub(r"\s+", " ", text).strip()
    if words(text) <= limit:
        return text
    sents = SENT_SPLIT.split(text)
    out = ""
    for s in sents:
        cand = (out + " " + s).strip()
        if words(cand) > limit:
            break
        out = cand
    if out and words(out) >= 8:
        return out
    return " ".join(text.split()[:limit]).rstrip(",;:") + "…"


GOOD_WORDS = re.compile(r"\b(filme|obra|cinema|cl[aá]ssico|diretor|dire[cç][aã]o|pel[ií]cula|director|direcci[oó]n|obra maestra|cine|narrativa|hist[oó]ria|historia|roteiro|gui[oó]n|atua[cç][aã]o|interpreta|imagens|im[aá]genes)\b", re.I)
BAD_START = re.compile(r"^(e|mas|por[eé]m|ent[aã]o|pero|y|entonces|ela|ele|eles|elas|isso|isto|esto|eso|ella|[eé]l|ellos|aqui|aqu[ií]|ali|n[oó]s|nosotros|eu|yo|voc[eê])\b", re.I)


def pick_sentence(paragraphs, limit=25, titles=()):
    """Frase avaliativa da crítica: na segunda metade do texto (conclusão), uma frase de 10 a `limit`
    palavras que fale do filme/obra e não dependa da frase anterior."""
    cands = []
    for p in paragraphs:
        for s in SENT_SPLIT.split(p.strip()):
            s = s.strip()
            n = words(s)
            if 10 <= n <= limit and not re.search(r"^\S+(\s\S+){0,2}:|https?://|\b(spoiler|clique|assista|leia também|lea también|veja também|trailer)\b", s, re.I) \
                    and s[0].isupper() and s[-1] in ".!?" and not re.search(r"[\"“”«»]", s[:1]):
                cands.append(s)
    if not cands:
        return None
    half = cands[len(cands) // 2:] or cands
    def score(s):
        ns = norm(s)
        mention = any(norm(t) and norm(t) in ns for t in titles)
        return (3 if EVAL_WORDS.search(s) else 0) + (2 if mention else 0) + (1 if GOOD_WORDS.search(s) else 0) + (0 if BAD_START.search(s) else 1)
    best = max(range(len(half)), key=lambda i: (score(half[i]), i))
    if score(half[best]) < 3:
        allbest = max(range(len(cands)), key=lambda i: (score(cands[i]), -i))
        if score(cands[allbest]) < 3:
            return None
        return cands[allbest]
    return half[best]


EVAL_WORDS = re.compile(r"(obra-prima|magistral|brilhant|genial|impression|excelente|marcante|inesquec|fascinante|essencial|extraordin|poderos|memor[aá]vel|ousad|sens[ií]vel|belo|bela|lindo|perfeit|cl[aá]ssico|maestr|obra maestra|imprescindible|inolvidable|deslumbrante|brillante|fascinante|hermos|perfect)", re.I)


TITLE_YEAR = re.compile(r"\b(18[89]\d|19\d\d|20\d\d)\b")


def clean_title(t, directors=(), originals=()):
    t = html.unescape(t)
    t = re.sub(r"^[^|]*\|\s*", "", t) if "|" in t else t
    t = re.sub(r"^(cr[ií]tica|cineclub|cine club)\s*[:\-–]?\s*(by benq\s*:)?\s*", "", t, flags=re.I)
    for d in directors:
        t = re.sub(r",?\s*(de\s+|by\s+)?" + re.escape(d) + r"\s*,?", " ", t, flags=re.I)
    t = re.sub(r",?\s*\b(18[89]\d|19\d\d|20\d\d)\s*$", "", t.strip())
    m = re.search(r"\s*\(([^()]*)\)\s*$", t)
    if m and (TITLE_YEAR.search(m.group(1)) or any(norm(o) and norm(o) in norm(m.group(1)) for o in originals)
              or re.fullmatch(r"\d+", m.group(1).strip())):
        t = t[:m.start()]
    return norm(t)


def title_matches(post_title, variants, year, directors=(), originals=()):
    ys = set(TITLE_YEAR.findall(post_title))
    if ys and str(year) not in ys:
        return False
    ct = clean_title(post_title, directors, originals)
    for v in variants:
        nv = norm(v)
        if ct == nv or (":" in v and ct == norm(v.split(":")[0])) or ct == norm(re.sub(r"^(the|a|an|o|os|as|el|la|los|las|le|les|il|der|die|das)\s+", "", v, flags=re.I)):
            return True
    return False


def link_ok(url):
    """True se a página existe: 200 direto, ou 403/429 (anti-robô) com cópia no Wayback Machine."""
    code, _ = curl(url, timeout=25)
    if code == 200:
        return True
    if code in (403, 429, 0, 401, 503, 202):
        c2, body = curl("https://archive.org/wayback/available?url=" + urllib.parse.quote(url, safe=""), ua=BOT_UA)
        try:
            snap = json.loads(body).get("archived_snapshots", {}).get("closest")
        except Exception:
            snap = None
        return bool(snap and snap.get("available") and str(snap.get("status")) == "200")
    return False


def title_variants(e, *keys):
    out = []
    for k in keys:
        t = e.get(k)
        if t and norm(t) not in [norm(x) for x in out]:
            out.append(t)
    return out


def wp_reviews(site, e, lang_titles, name, reviewer=None, max_n=2, title_must=r"cr[ií]tica", with_year=True):
    """Busca no WordPress (API REST) por título + ano e confere título/ano/diretor no texto."""
    found = []
    year = str(e["year"])
    dir_last = [norm(d).split()[-1] for d in e.get("directors", []) if d]
    seen = set()
    for t, q in [(t, q) for t in lang_titles for q in ((f"{t} {year}", t) if with_year else (t,))]:
        url = f"https://{site}/wp-json/wp/v2/posts?" + urllib.parse.urlencode(
            {"search": q, "per_page": 10, "orderby": "relevance", "_fields": "id,link,title,date"})
        code, body = curl(url)
        if code != 200:
            continue
        try:
            posts = json.loads(body)
        except Exception:
            continue
        for p in posts if isinstance(posts, list) else []:
            link = p.get("link")
            if link in seen:
                continue
            seen.add(link)
            ttl = strip_tags(p["title"]["rendered"]).strip()
            nt = norm(ttl)
            if title_must and not re.search(title_must, ttl, re.I):
                continue
            if not title_matches(ttl, lang_titles, year, e.get("directors", []), [e.get("original_title") or "", e.get("title_en") or ""]):
                continue
            c2, b2 = curl(f"https://{site}/wp-json/wp/v2/posts/{p['id']}?" + urllib.parse.urlencode(
                {"_fields": "content,author,_links", "_embed": "author"}))
            if c2 != 200:
                continue
            try:
                p.update(json.loads(b2))
            except Exception:
                continue
            content = strip_tags(p["content"]["rendered"])
            ncontent = norm(content)
            # o ano no título, ou ano + diretor no texto
            if year not in ttl and not (year in content and any(dl in ncontent for dl in dir_last)):
                continue
            if dir_last and not any(dl in ncontent or dl in nt for dl in dir_last):
                continue
            paras = [x for x in content.split("\n") if words(x) > 15]
            sent = pick_sentence(paras, titles=lang_titles)
            if not sent:
                continue
            author = None
            try:
                author = p["_embedded"]["author"][0]["name"]
            except Exception:
                pass
            found.append({"site": name, "reviewer": reviewer, "author": author, "url": link,
                          "date": (p.get("date") or "")[:10], "excerpt": sent, "title": ttl})
            if len(found) >= max_n:
                return found
    return found


def blogger_reviews(site, e, lang_titles, name, max_n=1):
    found = []
    year = str(e["year"])
    dir_last = [norm(d).split()[-1] for d in e.get("directors", []) if d]
    seen = set()
    for t in lang_titles:
        url = f"https://{site}/feeds/posts/default?" + urllib.parse.urlencode({"q": f"{t}", "alt": "json", "max-results": 10})
        code, body = curl(url)
        if code != 200:
            continue
        try:
            entries = json.loads(body)["feed"].get("entry", [])
        except Exception:
            continue
        for en in entries:
            ttl = html.unescape(en["title"]["$t"])
            link = next((l["href"] for l in en["link"] if l["rel"] == "alternate"), None)
            if not link or link in seen:
                continue
            seen.add(link)
            if not title_matches(ttl, lang_titles, year, e.get("directors", []), [e.get("original_title") or "", e.get("title_en") or ""]):
                continue
            content = strip_tags(en.get("content", {}).get("$t", ""))
            ncontent = norm(content)
            if year not in ttl and year not in content:
                continue
            if dir_last and not any(dl in ncontent or dl in norm(ttl) for dl in dir_last):
                continue
            if re.search(r"\b(mejores|peores|ranking|top \d+|\d+ películas)\b", ttl, re.I):
                continue
            paras = [x for x in content.split("\n") if words(x) > 15]
            sent = pick_sentence(paras, titles=lang_titles)
            if not sent:
                continue
            author = (en.get("author") or [{}])[0].get("name", {}).get("$t")
            if author in ("EAM", "Unknown", None):
                author = None
            found.append({"site": name, "author": author, "url": link, "date": en.get("published", {}).get("$t", "")[:10],
                          "excerpt": sent, "title": ttl})
            if len(found) >= max_n:
                return found
    return found


TOP_EN = ["RogerEbert.com", "Chicago Sun-Times", "New York Times", "Guardian", "Variety", "Time Out", "Empire",
          "Village Voice", "Chicago Reader", "New Yorker", "Washington Post", "Los Angeles Times", "Slant Magazine",
          "Observer", "TIME Magazine", "Sight & Sound", "Hollywood Reporter", "Telegraph", "Film Comment",
          "Criterion", "BBC", "Independent", "Wall Street Journal", "NPR", "AV Club", "Boston Globe"]


def rt_reviews(e, max_n=3):
    rt = e.get("rt")
    if not rt or not rt.startswith("m/"):
        return []
    code, body = curl(f"https://www.rottentomatoes.com/{rt}")
    if code != 200:
        return []
    out = []
    for card in re.findall(r"<review-card-critic(.*?)</review-card-critic>", body, re.S):
        name = re.search(r'slot="name"[^>]*>\s*(.*?)\s*</rt-link>', card, re.S)
        pub = re.search(r'slot="publication"[^>]*>\s*(.*?)\s*</rt-link>', card, re.S)
        date = re.search(r'slot="timestamp">([^<]*)<', card)
        quote = re.search(r'slot="review">(.*?)</span>', card, re.S)
        link = re.search(r'slot="review-link"[^>]*href="([^"]+)"', card)
        rating = re.search(r'slot="rating">.*?<span>([^<]*)</span>', card, re.S)
        if not (pub and quote and link):
            continue
        q = html.unescape(re.sub(r"<[^>]+>", "", quote.group(1))).strip()
        if not q or words(q) < 6:
            continue
        out.append({"site": html.unescape(pub.group(1).strip()), "author": html.unescape(name.group(1).strip()) if name else None,
                    "url": html.unescape(link.group(1)), "date_rt": date.group(1).strip() if date else None,
                    "excerpt": q, "rating_rt": rating.group(1).strip() if rating else ""})

    def rank(r):
        for i, n in enumerate(TOP_EN):
            if n.lower() in r["site"].lower():
                return i
        return 100

    out.sort(key=rank)
    res = []
    used_sites = set()
    for r in out:
        if r["site"] in used_sites or re.search(r"newspapers\.com|youtube\.com|youtu\.be|podcast|spotify|apple\.com|filmspotting", r["url"] + r["site"], re.I):
            continue
        if not r["excerpt"][:1].isupper() and not r["excerpt"][:1] in "\"'“‘":
            continue
        if re.search(r"[áéíóúñ¿¡ãõç]", r["excerpt"]):  # só inglês aqui
            continue
        host = urllib.parse.urlsplit(r["url"]).netloc.lower()
        if re.search(r"\.(es|mx|pe|ar|cl|co|br|pt|it|fr|de|nl|se|dk|no|pl|ru|jp|kr)$", host):
            continue
        if not link_ok(r["url"]):
            continue
        code, body = curl(r["url"], timeout=25)
        lang = re.search(r"<html[^>]*\blang=[\"']?([a-zA-Z-]+)", body or "")
        if code == 200 and lang and not lang.group(1).lower().startswith("en"):
            continue
        r["excerpt"] = shorten(r["excerpt"])
        used_sites.add(r["site"])
        res.append(r)
        if len(res) >= max_n:
            break
    return res


def rating_from_rt(s):
    m = re.match(r"\s*([\d.]+)\s*/\s*([\d.]+)", s or "")
    if m:
        try:
            v = float(m.group(1)) / float(m.group(2)) * 5
            return round(v * 2) / 2
        except ZeroDivisionError:
            return None
    m = re.match(r"\s*([A-F])([+-]?)\s*$", s or "")
    if m:
        base = {"A": 5, "B": 4, "C": 3, "D": 2, "F": 1}[m.group(1)]
        return base - (0.5 if m.group(2) == "-" else 0)
    return None


def collect_reviews(e):
    pt_titles = title_variants(e, "title_pt", "original_title")
    es_titles = title_variants(e, "title_es", "original_title")
    res = {}
    pt = wp_reviews("www.planocritico.com", e, pt_titles, "Plano Crítico", reviewer="plano_critico", max_n=2)
    if len(pt) < 2:
        pt += wp_reviews("www.vertentesdocinema.com", e, pt_titles, "Vertentes do Cinema", max_n=1, title_must=None, with_year=False)
    res["pt"] = pt
    res["en"] = rt_reviews(e)
    es = wp_reviews("encadenados.org", e, es_titles, "Encadenados", max_n=2, title_must=None, with_year=False)
    es = [r for r in es if not re.search(r"/(festivales|editorial|puntuaciones|libros)/", r["url"])]
    if len(es) < 2:
        es += wp_reviews("miradasdecine.es", e, es_titles, "Miradas de Cine", max_n=1, title_must=None, with_year=False)
    if len(es) < 2:
        es += blogger_reviews("www.elantepenultimomohicano.com", e, es_titles, "El Antepenúltimo Mohicano")
    res["es"] = es[:2]
    return res


def cmd_reviews(args):
    index = load(INDEX)
    reviews = load(REVIEWS, {})
    ids = [int(x) for x in args.ids.split(",")] if args.ids else ordered_ids(index)
    todo = [i for i in ids if args.refresh or str(i) not in reviews]
    print(f"{len(todo)} filmes")

    def work(mid):
        try:
            return mid, collect_reviews(index[str(mid)])
        except Exception as ex:  # noqa
            return mid, {"error": repr(ex)}

    done = 0
    with concurrent.futures.ThreadPoolExecutor(args.workers) as ex:
        for mid, r in ex.map(work, todo):
            reviews[str(mid)] = r
            done += 1
            if done % 25 == 0:
                save(REVIEWS, reviews)
                print(f"  {done}/{len(todo)}", flush=True)
    save(REVIEWS, reviews)
    tot = {k: sum(1 for r in reviews.values() if r.get(k)) for k in ("pt", "en", "es")}
    print("filmes com críticas:", tot)


# --------------------------------------------------------------------------------------
# YouTube (filme completo, legalmente disponível)
# --------------------------------------------------------------------------------------

# Vídeos encontrados automaticamente mas rejeitados na revisão manual (id TMDB do filme -> motivo).
REJECTED_VIDEOS = {
    "149155": "filme errado (Rafoo Chakkar no lugar de Harvest: 3000 Years)",
    "37291": "filme errado (Betrayal of Trust)",
    "43685": "filme errado (Деловые люди no lugar de Человек-амфибия)",
    "129555": "Raja Harishchandra sobrevive só em parte; vídeo de 53 min duvidoso",
    "228": "versão colorizada",
    "39950": "filme perdido; vídeo é reconstrução",
}


def yt_ok(mid, y):
    return bool(y and y.get("video")) and str(mid) not in REJECTED_VIDEOS


# Canais oficiais/arquivos que publicam filmes completos com autorização dos detentores.
OFFICIAL_CHANNELS = {
    "Mosfilm": "Canal oficial do estúdio Mosfilm",
    "Киноконцерн \"Мосфильм\"": "Canal oficial do estúdio Mosfilm",
    "Ленфильм": "Canal oficial do estúdio Lenfilm",
    "Киностудия Горького": "Estúdio Gorky (oficial)",
    "Lenfilm": "Canal oficial do estúdio Lenfilm",
    "Paramount Vault": "Canal oficial da Paramount",
    "Cohen Media Group": "Cohen Media Group",
    "Film Preservation": "National Film Preservation Foundation",
    "National Film Preservation Foundation": "National Film Preservation Foundation",
    "Library of Congress": "Library of Congress",
    "EYE Filmmuseum": "EYE Filmmuseum",
    "BFI": "British Film Institute",
    "Gaumont Pathé archives": "Gaumont Pathé Archives",
    "Lobster Films": "Lobster Films",
    "Cinémathèque française": "Cinémathèque française",
    "Fondazione Cineteca di Bologna": "Cineteca di Bologna",
    "Deutsche Kinemathek": "Deutsche Kinemathek",
    "Friedrich-Wilhelm-Murnau-Stiftung": "Murnau-Stiftung",
    "FilmRise Movies": "FilmRise (distribuidora)",
    "Shout! Studios": "Shout! Studios",
    "Cinemateca Brasileira": "Cinemateca Brasileira",
    "Films Division": "Films Division of India",
    "Ritwik Ghatak": None,
    "Satyajit Ray Society": None,
    "Rajshri": "Rajshri Productions",
    "Goldmines Bollywood": None,
    "Shemaroo": "Shemaroo Entertainment",
    "Ultra Movie Parlour": None,
    "Studio Ghibli": None,
}

BAD_WORDS = r"\b(trailer|teaser|clip|scene|escena|cena|featurette|review|reseña|analysis|análise|ensaio|essay|explained|reaction|behind|making of|remake|colorized|colourized|colorizada|colorida|ai upscaled|4k ai|restored by ai|shorts|recap|resumen|resumo|parody|musical score only|audiobook|audio ?book|librivox|read by|part \d|parte \d|pt\. ?\d|remastered trailer)\b"


def parse_duration(s):
    parts = [int(x) for x in (s or "").split(":") if x.isdigit()]
    tot = 0
    for p in parts:
        tot = tot * 60 + p
    return tot


def us_public_domain(e):
    """Domínio público nos EUA por idade: obras publicadas até 1930 (em 2026; 95 anos)."""
    return 0 < (e.get("year") or 0) <= time.localtime().tm_year - 96


def pd_list_titles():
    """Filmes da 'List of films in the public domain in the United States' (Wikipedia en)."""
    path = os.path.join(HERE, "pd_us_titles.json")
    if os.path.exists(path):
        return load(path)
    out = []
    code, body = curl("https://en.wikipedia.org/w/api.php?" + urllib.parse.urlencode(
        {"action": "parse", "page": "List of films in the public domain in the United States", "prop": "wikitext",
         "format": "json", "formatversion": 2}), ua=BOT_UA)
    wt = json.loads(body)["parse"]["wikitext"]
    for row in wt.split("\n|-"):
        m = re.search(r"''\[\[([^\]|]+)(?:\|([^\]]+))?\]\]''", row)
        y = re.search(r"\b(19\d\d|20\d\d)\b", row)
        if m and y:
            out.append({"article": m.group(1), "title": m.group(2) or m.group(1), "year": int(y.group(1))})
    save(path, out)
    return out


def yt_search(q):
    url = "https://www.youtube.com/results?hl=en&gl=US&search_query=" + urllib.parse.quote(q)
    code, body = curl(url, extra=["-H", "Accept-Language: en-US,en;q=0.9"])
    m = re.search(r"var ytInitialData = (\{.*?\});</script>", body)
    if not m:
        return []
    data = json.loads(m.group(1))
    out = []

    def walk(n):
        if isinstance(n, dict):
            if "videoRenderer" in n:
                v = n["videoRenderer"]
                badges = json.dumps(v.get("ownerBadges", []))
                out.append({
                    "id": v.get("videoId"),
                    "title": "".join(r.get("text", "") for r in v.get("title", {}).get("runs", [])),
                    "channel": "".join(r.get("text", "") for r in v.get("ownerText", {}).get("runs", [])),
                    "duration": parse_duration(v.get("lengthText", {}).get("simpleText", "")),
                    "views": v.get("viewCountText", {}).get("simpleText", ""),
                    "verified": "VERIFIED" in badges or "OFFICIAL_ARTIST" in badges,
                })
            for x in n.values():
                walk(x)
        elif isinstance(n, list):
            for x in n:
                walk(x)

    walk(data)
    return out


def oembed(vid):
    code, body = curl("https://www.youtube.com/oembed?format=json&url=" +
                      urllib.parse.quote(f"https://www.youtube.com/watch?v={vid}", safe=""), ua=BOT_UA)
    if code != 200:
        return None
    try:
        return json.loads(body)
    except Exception:
        return None


def find_youtube(e, pd_titles):
    year = e.get("year") or 0
    runtime = e.get("runtime") or 0
    pd_age = us_public_domain(e)
    pd_listed = any(abs(p["year"] - year) <= 1 and (norm(p["title"]) in (norm(e.get("title_en")), norm(e.get("original_title"))))
                    for p in pd_titles)
    titles = title_variants(e, "original_title", "title_en")
    best = None
    for t in titles:
        for q in (f"{t} {year} full movie", f"{t} {year}"):
            for v in yt_search(q)[:12]:
                nt = norm(v["title"])
                if not any(norm(x) in nt for x in titles):
                    continue
                if re.search(BAD_WORDS, v["title"], re.I):
                    continue
                official = next((k for k in OFFICIAL_CHANNELS if k.lower() == v["channel"].lower()), None)
                years = [int(y) for y in re.findall(r"\b(18[89]\d|19\d\d|20[0-2]\d)\b", v["title"])]
                if years and not any(abs(y - year) <= 1 for y in years):
                    continue
                dir_last = [norm(d).split()[-1] for d in e.get("directors", []) if d and norm(d)]
                if not official and not years and not any(dl in nt for dl in dir_last):
                    continue
                if not runtime and not official:
                    continue
                if runtime:
                    ratio = v["duration"] / (runtime * 60)
                    lo = 0.6 if year < 1930 else 0.85  # cópias de filmes mudos variam de velocidade
                    if not (lo <= ratio <= 1.45):
                        continue
                elif v["duration"] < 40 * 60:
                    continue
                if not (pd_age or pd_listed or official):
                    continue
                if official is None and not (pd_age or pd_listed):
                    continue
                score = (2 if official else 0) + (1 if v["verified"] else 0)
                views = int(re.sub(r"\D", "", v["views"]) or 0)
                cand = (score, views, v)
                if best is None or cand[:2] > best[:2]:
                    best = cand
            if best:
                break
        if best:
            break
    if not best:
        return None
    v = best[2]
    meta = oembed(v["id"])
    if not meta:
        return None
    reason = "official" if best[0] >= 2 else ("pd_age" if pd_age else "pd_list")
    return {"video": v["id"], "title": meta.get("title"), "channel": meta.get("author_name"), "duration": v["duration"],
            "reason": reason}


def cmd_youtube(args):
    index = load(INDEX)
    yt = load(YOUTUBE, {})
    pd_titles = pd_list_titles()
    ids = [int(x) for x in args.ids.split(",")] if args.ids else ordered_ids(index)
    cands = []
    for mid in ids:
        e = index[str(mid)]
        if str(mid) in yt and not args.refresh:
            continue
        cands.append(e)
    print(f"{len(cands)} filmes a verificar")

    def work(e):
        try:
            return e["id"], find_youtube(e, pd_titles)
        except Exception as ex:  # noqa
            return e["id"], {"error": repr(ex)}

    n = 0
    with concurrent.futures.ThreadPoolExecutor(args.workers) as ex:
        for mid, r in ex.map(work, cands):
            yt[str(mid)] = r
            n += 1
            if n % 50 == 0:
                save(YOUTUBE, yt)
                print(f"  {n}/{len(cands)}", flush=True)
    save(YOUTUBE, yt)
    print("com YouTube:", sum(1 for k, v in yt.items() if yt_ok(k, v)))


# --------------------------------------------------------------------------------------
# Wikipédia (material para as curiosidades)
# --------------------------------------------------------------------------------------

KEEP_SECTIONS = r"(production|background|development|filming|casting|writing|pre-production|music|release|reception|legacy|influence|restoration|preservation|accolades|awards|controvers|censorship|box office|trivia|produ[cç][aã]o|recep[cç][aã]o|curiosidades|lan[cç]amento|bastidores|pr[eê]mios)"


def wiki_sections(url, max_chars=3500):
    if not url:
        return ""
    host = urllib.parse.urlsplit(url).netloc
    title = urllib.parse.unquote(url.rsplit("/wiki/", 1)[1])
    code, body = curl(f"https://{host}/w/api.php?" + urllib.parse.urlencode(
        {"action": "query", "prop": "extracts", "explaintext": 1, "titles": title, "format": "json", "formatversion": 2,
         "redirects": 1}), ua=BOT_UA)
    if code != 200:
        return ""
    try:
        text = json.loads(body)["query"]["pages"][0].get("extract", "")
    except Exception:
        return ""
    parts = re.split(r"\n(={2,4} [^=]+ ={2,4})\n", text)
    out = [parts[0][:700]]
    for i in range(1, len(parts) - 1, 2):
        head, sec = parts[i], parts[i + 1]
        if re.search(KEEP_SECTIONS, head, re.I) and sec.strip():
            out.append(head.strip("= ").upper() + ": " + re.sub(r"\s+", " ", sec.strip()))
    s = "\n".join(out)
    return s[:max_chars]


def cmd_wiki(args):
    global CACHE_ONLY
    CACHE_ONLY = not args.fetch
    index = load(INDEX)
    for mid in [int(x) for x in args.ids.split(",")]:
        e = index[str(mid)]
        print(f"######## {mid} | {e['original_title']} ({e['year']}) | BR: {e['title_pt']} | dir. {', '.join(e['directors'])}")
        print("EN:", e.get("en"), "| PT:", e.get("pt"))
        print(wiki_sections(e.get("en"), args.chars))
        if args.pt and e.get("pt"):
            print("--- PT:", wiki_sections(e.get("pt"), 1500))
        print()


def expand(v):
    """Formato compacto dos arquivos curated/*.json -> formato completo.
    {"src": [urls], "f": [[pt_t, pt_d, en_t, en_d, es_t, es_d], ...], "q": [pt, autor, en, autor_en, es, autor_es]}"""
    if "f" not in v and "q" not in v:
        return v
    out = {"sources": v.get("src", []), "facts": []}
    for f in v.get("f", []):
        out["facts"].append({"title": f[0], "text": f[1], "en": {"title": f[2], "text": f[3]}, "es": {"title": f[4], "text": f[5]}})
    q = v.get("q")
    if q:
        out["quote"] = {"text": q[0], "author": q[1], "en": {"text": q[2], "author": q[3]}, "es": {"text": q[4], "author": q[5]}}
    return out


def cmd_prefetch(args):
    """Baixa (com calma, 1 pedido/s) os artigos da Wikipédia para o cache, na ordem de prioridade."""
    index = load(INDEX)
    for i, mid in enumerate(ordered_ids(index)):
        e = index[str(mid)]
        for k in ("en",) + (("pt",) if args.pt else ()):
            if e.get(k):
                wiki_sections(e[k])
                time.sleep(args.sleep)
        if i % 50 == 0:
            print(i, flush=True)


def curated_all():
    data = {}
    for f in sorted(glob.glob(os.path.join(CURATED, "*.json"))):
        for k, v in json.load(open(f)).items():
            data[k] = expand(v)
    return data


def cmd_todo(args):
    index = load(INDEX)
    cur = curated_all()
    spec = json.load(open(os.path.join(ASSETS, "specials", "movies.json")))
    has = {m["id"] for g in spec for m in g["movies"] if m.get("did_you_know_list")}
    ids = [i for i in ordered_ids(index) if str(i) not in cur and i not in has]
    print(f"faltam {len(ids)} filmes")
    print(",".join(str(i) for i in ids[:args.n]))


# --------------------------------------------------------------------------------------
# Aplicação no JSON do app
# --------------------------------------------------------------------------------------

LANG_TAG = {"pt": "pt", "en": "en", "es": "es"}


def review_entry(r, lang):
    entry = {"reviewer_site_name": r["site"], "review_url": r["url"], "review_description": r["excerpt"]}
    if r.get("reviewer"):
        entry["reviewer"] = r["reviewer"]
    entry["reviewer_name"] = r.get("author") or r["site"]
    if r.get("date"):
        entry["date"] = r["date"]
    elif r.get("date_rt"):
        try:
            entry["date"] = time.strftime("%Y-%m-%d", time.strptime(r["date_rt"], "%b %d, %Y"))
        except ValueError:
            pass
    rating = rating_from_rt(r.get("rating_rt")) if lang == "en" else None
    if rating:
        entry["review_rating"] = rating
    return entry


# --------------------------------------------------------------------------------------
# Referências principais (grupo novo em references.json)
# --------------------------------------------------------------------------------------

REF_GROUP = {"pt": "Curiosidades e críticas", "en": "Trivia and reviews", "es": "Curiosidades y críticas"}
# (pt, en, es, url) — fontes principais das seções extras da tela de detalhes do filme.
EXTRA_REFS = [
    ("Wikipedia (artigos em inglês de cada filme) — fonte dos fatos de \"Você sabia?\"",
     "Wikipedia (English article for each film) — source of the \"Did you know?\" facts",
     "Wikipedia (artículo en inglés de cada película) — fuente de los datos de \"¿Sabías que...?\"",
     "https://en.wikipedia.org/"),
    ("Wikipédia em português", "Portuguese Wikipedia", "Wikipedia en portugués", "https://pt.wikipedia.org/"),
    ("AFI's 100 Years...100 Movie Quotes — citações de filmes", "AFI's 100 Years...100 Movie Quotes — film quotes",
     "AFI's 100 Years...100 Movie Quotes — citas de películas", "https://www.afi.com/afis-100-years-100-movie-quotes/"),
    ("Plano Crítico — críticas em português", "Plano Crítico — reviews in Portuguese", "Plano Crítico — críticas en portugués",
     "https://www.planocritico.com/"),
    ("Vertentes do Cinema — críticas em português", "Vertentes do Cinema — reviews in Portuguese",
     "Vertentes do Cinema — críticas en portugués", "https://www.vertentesdocinema.com/"),
    ("Rotten Tomatoes — críticas em inglês", "Rotten Tomatoes — reviews in English", "Rotten Tomatoes — críticas en inglés",
     "https://www.rottentomatoes.com/"),
    ("Encadenados — críticas em espanhol", "Encadenados — reviews in Spanish", "Encadenados — críticas en español",
     "https://www.encadenados.org/"),
    ("Miradas de Cine — críticas em espanhol", "Miradas de Cine — reviews in Spanish", "Miradas de Cine — críticas en español",
     "https://www.miradas.net/"),
    ("El Antepenúltimo Mohicano — críticas em espanhol", "El Antepenúltimo Mohicano — reviews in Spanish",
     "El Antepenúltimo Mohicano — críticas en español", "https://antepenultimomohicano.blogspot.com/"),
    ("Wikidata — identificadores dos filmes (Wikipedia, Rotten Tomatoes)", "Wikidata — film identifiers (Wikipedia, Rotten Tomatoes)",
     "Wikidata — identificadores de las películas (Wikipedia, Rotten Tomatoes)", "https://www.wikidata.org/"),
    ("U.S. Copyright Office — duração do direito autoral (filmes em domínio público no YouTube)",
     "U.S. Copyright Office — copyright duration (public-domain films on YouTube)",
     "U.S. Copyright Office — duración de los derechos de autor (películas de dominio público en YouTube)",
     "https://www.copyright.gov/help/faq/faq-duration.html"),
    ("Wikipedia — lista de filmes em domínio público nos EUA", "Wikipedia — list of films in the public domain in the United States",
     "Wikipedia — lista de películas de dominio público en EE. UU.",
     "https://en.wikipedia.org/wiki/List_of_films_in_the_public_domain_in_the_United_States"),
]
AVAILABLE = {"pt": "Disponível em", "en": "Available at", "es": "Disponible en"}


def ref_text(desc, url, lang="pt"):
    return (f"{desc}. {AVAILABLE[lang]}: <a href=\"https://_{{'type': 'online', 'id': 11523, 'url': '{url}'}}\">"
            f"{url}</a>.")


def apply_references():
    path = os.path.join(ASSETS, "references.json")
    refs = json.load(open(path))
    refs = [g for g in refs if g.get("name") != REF_GROUP["pt"]]
    refs.append({"name": REF_GROUP["pt"], "references": [{"type": "text", "text": ref_text(pt, url)} for pt, _, _, url in EXTRA_REFS]})
    json.dump(refs, open(path, "w"), ensure_ascii=False, indent=2)
    open(path, "a").write("\n")


def cmd_apply(args):
    path = os.path.join(ASSETS, "specials", "movies.json")
    spec = json.load(open(path))
    index = load(INDEX)
    reviews = load(REVIEWS, {})
    yt = load(YOUTUBE, {})
    cur = curated_all()
    by_id = {}
    for g in spec:
        for m in g["movies"]:
            by_id[m["id"]] = m
    # Filmes de "Vale a conferida" fora da lista 1001: grupos por era da história (sem link para a lista 1001)
    hist_groups = {g["historyMainTopicID"]: g for g in spec if g["list"].startswith("HISTORY ")}
    for e in sorted(index.values(), key=lambda e: (e.get("year") or 0, e["id"])):
        if e["id"] in by_id:
            continue
        era = e.get("vac")
        if not era:
            continue
        g = hist_groups.get(era)
        if g is None:
            g = {"list": f"HISTORY {era}", "milMoviesMainTopicID": 0, "historyMainTopicID": era, "movies": []}
            hist_groups[era] = g
            spec.append(g)
        m = {"id": e["id"], "original_title": e["original_title"], "watchOn": []}  # preenchido por i18n.py watch-pt
        g["movies"].append(m)
        by_id[e["id"]] = m
    stats = {"did_you_know": 0, "quote": 0, "reviews": 0, "youtube": 0}
    for mid, m in by_id.items():
        k = str(mid)
        c = cur.get(k)
        if c and c.get("facts"):
            m["did_you_know_list"] = [{"title": f["title"], "description": f["text"]} for f in c["facts"]]
            stats["did_you_know"] += 1
        if c and c.get("quote"):
            m["quote"] = {"type": "quote", "quote": {"quote": c["quote"]["text"], "author": c["quote"]["author"]}}
        if m.get("quote"):
            stats["quote"] += 1
        r = reviews.get(k) or {}
        results = []
        old = {x.get("language"): x for x in m.get("review_results", [])}
        for lang, iso_old in (("pt", "pt-BR"), ("en", "en-US"), ("es", "es")):
            items = []
            seen = set()
            for x in (old.get(iso_old) or old.get(lang) or {}).get("reviews", []):
                items.append(x)
                seen.add(x.get("review_url"))
            for x in r.get(lang) or []:
                if x["url"] not in seen and len(items) < 3:
                    items.append(review_entry(x, lang))
                    seen.add(x["url"])
            if items:
                results.append({"language": lang, "reviews": items})
        if results:
            m["review_results"] = results
            stats["reviews"] += 1
        y = yt.get(k)
        w = [x for x in (m.get("watchOn") or []) if x.get("type") != "youtube_free"]
        if yt_ok(k, y):
            link = f"https://www.youtube.com/watch?v={y['video']}"
            m["watchOn"] = [{"type": "youtube_free", "link": link}] + w
            stats["youtube"] += 1
        elif m.get("watchOn") is not None:
            m["watchOn"] = w
    # grupos: décadas da lista 1001 primeiro, depois as eras da história em ordem
    spec.sort(key=lambda g: (1, g["historyMainTopicID"]) if g["list"].startswith("HISTORY ") else (0, 0))
    # ordem estável de chaves
    order = ["id", "title", "original_title", "review_results", "watchOn", "did_you_know_list", "quote", "block_special"]
    for g in spec:
        g["movies"] = [{kk: m[kk] for kk in order if kk in m} | {kk: v for kk, v in m.items() if kk not in order} for m in g["movies"]]
    json.dump(spec, open(path, "w"), ensure_ascii=False, indent=2)
    print("aplicado:", stats, "| filmes no arquivo:", len(by_id))
    apply_references()
    write_translations(cur)


def write_translations(cur):
    """Grava no catálogo do content-i18n as traduções en/es escritas junto com o pt (curated)."""
    sys.path.insert(0, os.path.join(ROOT, "content-i18n"))
    import i18n_lib as L  # noqa
    for lang in ("en", "es"):
        items = []
        for k, c in cur.items():
            for f in c.get("facts", []):
                tr = f.get(lang) or {}
                if tr.get("title"):
                    items.append((f["title"], tr["title"]))
                if tr.get("text"):
                    items.append((f["text"], tr["text"]))
            q = c.get("quote")
            if q and (q.get(lang) or {}).get("text"):
                items.append((q["text"], q[lang]["text"]))
                if q[lang].get("author"):
                    items.append((q["author"], q[lang]["author"]))
        items.append((REF_GROUP["pt"], REF_GROUP[lang]))
        li = ("pt", "en", "es").index(lang)
        for r in EXTRA_REFS:
            items.append((ref_text(r[0], r[3]), ref_text(r[li], r[3], lang)))
        batch = []
        for src, dst in items:
            masked, _ = L.mask(src)
            dmasked, _ = L.mask(dst)
            batch.append({"id": L.entry_id(masked), "src": masked, "t": dmasked})
        out = os.path.join(ROOT, "content-i18n", "work", f"{lang}-movie-extras.json")
        os.makedirs(os.path.dirname(out), exist_ok=True)
        json.dump({"lang": lang, "items": batch}, open(out, "w"), ensure_ascii=False, indent=1)
        print(f"traduções {lang}: {len(batch)} -> {out}")


def main():
    ap = argparse.ArgumentParser()
    sub = ap.add_subparsers(dest="cmd", required=True)
    sub.add_parser("index")
    p = sub.add_parser("reviews")
    p.add_argument("--ids")
    p.add_argument("--refresh", action="store_true")
    p.add_argument("--workers", type=int, default=6)
    p = sub.add_parser("youtube")
    p.add_argument("--ids")
    p.add_argument("--refresh", action="store_true")
    p.add_argument("--workers", type=int, default=4)
    p = sub.add_parser("wiki")
    p.add_argument("--ids", required=True)
    p.add_argument("--chars", type=int, default=3500)
    p.add_argument("--pt", action="store_true")
    p.add_argument("--fetch", action="store_true", help="baixa o que não estiver no cache (a Wikipédia limita pedidos)")
    p = sub.add_parser("prefetch")
    p.add_argument("--pt", action="store_true")
    p.add_argument("--sleep", type=float, default=1.0)
    p = sub.add_parser("todo")
    p.add_argument("--n", type=int, default=20)
    sub.add_parser("apply")
    a = ap.parse_args()
    {"index": cmd_index, "reviews": cmd_reviews, "youtube": cmd_youtube, "wiki": cmd_wiki, "todo": cmd_todo, "prefetch": cmd_prefetch,
     "apply": cmd_apply}[a.cmd](a)


if __name__ == "__main__":
    main()
