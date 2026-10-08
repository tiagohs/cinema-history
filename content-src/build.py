#!/usr/bin/env python3
"""
Gerador das páginas de história a partir do formato de autoria (content-src/pt/main_<era>/page_<n>.md).

Formato (seções começam com "# <tipo>"; linhas "chave: valor"; o corpo de "# text" é o próprio parágrafo):

    # sumario
    title: Título do capítulo
    description: Subtítulo/descrição do capítulo
    image: name=img_nome tmdb_movie=12345            (ou tmdb_path=/x.jpg, ou url=https://...)

    # text
    Parágrafo com {{m:550}} (filme) e {{p:287}} (pessoa). {{m:550|texto}} usa o texto dado, sem "(BR: …)".
    HTML simples permitido: <i>, <strong>, <br/>.

    # video          youtube: ID / title: / text: / source:
    # essay          youtube: ID / title: / description: / channel:
    # quote          quote: / author:
    # block          title: / image: / text: / link: URL | Texto do botão (opcional)
    # image          image: / title: / text: / source:
    # movies         ids TMDB separados por vírgula  -> lista "Vale a conferida…"
    # persons        ids TMDB separados por vírgula

Comandos:
    build.py check <arquivo.md>...   valida e mostra o texto final (links resolvidos, vídeos)
    build.py build [--era N]         gera JSON das páginas, sumários e imagens
"""
from __future__ import annotations

import io
import json
import os
import re
import sys
import time
import urllib.parse
import urllib.request

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
ASSETS = os.path.join(ROOT, "android/app/src/main/assets/local/pt")
DRAWABLE = os.path.join(ROOT, "android/app/src/main/res/drawable-xxhdpi")
CACHE = os.path.join(HERE, "cache")
KEY = "dac4d50f24dee29513738d8fa3470a3f"
UA = {"User-Agent": "CinemaHistoryContentBuilder/1.0 (https://github.com/tiagohs/cinema-history)"}

TIMELINE_TEXT = {
    7: ("Timeline de Acontecimentos de 2010 a 2019", "6"),
    8: ("Timeline de Acontecimentos de 2020 até hoje", "7"),
}

os.makedirs(CACHE, exist_ok=True)


# --------------------------------------------------------------------------------------
# Cache / rede
# --------------------------------------------------------------------------------------

def _cache_path(name):
    return os.path.join(CACHE, name + ".json")


def _load(name):
    p = _cache_path(name)
    return json.load(open(p)) if os.path.exists(p) else {}


def _save(name, data):
    # Vários agentes rodam em paralelo: mescla com o que está no disco e grava de forma atômica.
    path = _cache_path(name)
    try:
        disk = json.load(open(path)) if os.path.exists(path) else {}
    except Exception:
        disk = {}
    disk.update(data)
    data.update(disk)
    tmp = f"{path}.{os.getpid()}.tmp"
    json.dump(disk, open(tmp, "w"), ensure_ascii=False, indent=1, sort_keys=True)
    os.replace(tmp, path)


def http_json(url, headers=None):
    for attempt in range(5):
        try:
            req = urllib.request.Request(url, headers=headers or UA)
            with urllib.request.urlopen(req, timeout=30) as r:
                return json.load(r)
        except urllib.error.HTTPError as e:
            if e.code in (404, 401, 403, 400):
                return {"__error__": e.code}
            time.sleep(2 * (attempt + 1))
        except Exception:
            time.sleep(2 * (attempt + 1))
    return {"__error__": "network"}


_tmdb = _load("tmdb")


def tmdb(path, **params):
    params["api_key"] = KEY
    key = path + "?" + urllib.parse.urlencode(sorted((k, v) for k, v in params.items() if k != "api_key"))
    if key not in _tmdb:
        data = http_json(f"https://api.themoviedb.org/3{path}?" + urllib.parse.urlencode(params))
        _tmdb[key] = data
        _save("tmdb", _tmdb)
    return _tmdb[key]


def movie(mid, lang="pt-BR"):
    return tmdb(f"/movie/{mid}", language=lang)


def person(pid):
    return tmdb(f"/person/{pid}", language="en-US")


_yt = _load("youtube")


def youtube(vid):
    if vid not in _yt:
        url = "https://www.youtube.com/oembed?format=json&url=" + urllib.parse.quote(f"https://www.youtube.com/watch?v={vid}")
        _yt[vid] = http_json(url)
        _save("youtube", _yt)
    return _yt[vid]


# --------------------------------------------------------------------------------------
# Texto
# --------------------------------------------------------------------------------------

LATIN = re.compile(r"^[\u0000-ɏḀ-ỿ -⁯ -ÿ’'·–—]*$")


def year_of(m):
    return (m.get("release_date") or "")[:4]


def movie_anchor(mid, custom=None, errors=None):
    pt = movie(mid, "pt-BR")
    if "__error__" in pt:
        errors.append(f"filme {mid} não existe no TMDB")
        return f"[filme {mid}?]"
    en = movie(mid, "en-US")
    original = pt.get("original_title") or ""
    anchor = original if LATIN.match(original) else en.get("title") or original
    href = f"<a href=\"https://_{{type: 'screen', 'id': {mid}, 'screen_type': movie}}\">"
    if custom:
        return f"{href}{custom}</a>"
    y = year_of(pt)
    br = pt.get("title") or anchor
    if br.strip().lower() == anchor.strip().lower():
        return f"{href}{anchor}</a> ({y})"
    return f"{href}{anchor}</a> (BR: {br}, {y})"


def person_anchor(pid, custom=None, errors=None):
    p = person(pid)
    if "__error__" in p:
        errors.append(f"pessoa {pid} não existe no TMDB")
        return f"[pessoa {pid}?]"
    name = custom or p.get("name")
    return f"<a href=\"https://_{{type: 'screen', 'id': {pid}, 'screen_type': person}}\">{name}</a>"


TAG = re.compile(r"\{\{(m|p):(\d+)(?:\|([^}]*))?\}\}")


def render(text, errors):
    def sub(mt):
        kind, ident, custom = mt.group(1), int(mt.group(2)), mt.group(3)
        return movie_anchor(ident, custom, errors) if kind == "m" else person_anchor(ident, custom, errors)

    out = TAG.sub(sub, text.strip())
    if "{{" in out or "}}" in out:
        errors.append("tag malformada: " + out[:80])
    for tag in re.findall(r"</?([a-zA-Z]+)", out):
        if tag.lower() not in ("a", "i", "strong", "br", "b", "em"):
            errors.append(f"tag HTML não permitida <{tag}>")
    return out


# --------------------------------------------------------------------------------------
# Imagens
# --------------------------------------------------------------------------------------

def parse_image(spec, errors):
    opts = dict(kv.split("=", 1) for kv in spec.split() if "=" in kv)
    if "name" not in opts or not opts["name"].startswith("img_"):
        errors.append(f"imagem sem name=img_*: {spec}")
    if not any(k in opts for k in ("tmdb_movie", "tmdb_path", "url")):
        errors.append(f"imagem sem origem (tmdb_movie/tmdb_path/url): {spec}")
    return opts


def image_source_url(opts):
    if "tmdb_path" in opts:
        return "https://image.tmdb.org/t/p/w1280" + opts["tmdb_path"]
    if "tmdb_movie" in opts:
        imgs = tmdb(f"/movie/{opts['tmdb_movie']}/images", include_image_language="null,en")
        backs = imgs.get("backdrops") or []
        backs.sort(key=lambda b: (b.get("iso_639_1") is not None, -(b.get("vote_average") or 0)))
        if backs:
            return "https://image.tmdb.org/t/p/w1280" + backs[0]["file_path"]
        m = movie(opts["tmdb_movie"])
        return "https://image.tmdb.org/t/p/w1280" + (m.get("backdrop_path") or m.get("poster_path") or "")
    return opts["url"]


def existing_drawables():
    res = os.path.join(ROOT, "android/app/src/main/res")
    names = set()
    for d in os.listdir(res):
        if d.startswith("drawable"):
            names |= {os.path.splitext(f)[0] for f in os.listdir(os.path.join(res, d))}
    return names


_EXISTING = None
_generated = _load("images")


def fetch_image(opts, errors, write=True):
    global _EXISTING
    name = opts.get("name", "img_unknown")
    out = os.path.join(DRAWABLE, name + ".webp")
    if _EXISTING is None:
        _EXISTING = existing_drawables()
    source = image_source_url(opts) if any(k in opts for k in ("tmdb_movie", "tmdb_path", "url")) else None
    if name in _EXISTING and _generated.get(name) not in (None, source):
        errors.append(f"nome de imagem {name} já existe no app com outra imagem: escolha outro nome")
    elif name in _EXISTING and name not in _generated:
        errors.append(f"nome de imagem {name} já existe no app: escolha outro nome")
    if not write:
        return name
    if os.path.exists(out) and _generated.get(name) == source and not opts.get("refresh"):
        return name
    from PIL import Image
    url = source
    try:
        req = urllib.request.Request(url, headers=UA)
        with urllib.request.urlopen(req, timeout=60) as r:
            data = r.read()
        im = Image.open(io.BytesIO(data)).convert("RGB")
        if im.width > 1080:
            im = im.resize((1080, round(im.height * 1080 / im.width)), Image.LANCZOS)
        os.makedirs(DRAWABLE, exist_ok=True)
        im.save(out, "WEBP", quality=72, method=6)
        _generated[name] = url
        _save("images", _generated)
    except Exception as e:
        errors.append(f"falha ao baixar imagem {name} ({url}): {e}")
    return name


def local_image(name, height=None):
    style = {"scale_type": "center_crop"}
    if height:
        style["resize"] = {"height": height}
    return {"image_type": "local", "url": name, "style": style}


# --------------------------------------------------------------------------------------
# Parser
# --------------------------------------------------------------------------------------

def parse(path):
    sections, cur = [], None
    for raw in open(path, encoding="utf-8"):
        line = raw.rstrip("\n")
        m = re.match(r"^#\s+(\w+)\s*$", line)
        if m:
            cur = {"type": m.group(1).lower(), "fields": {}, "body": []}
            sections.append(cur)
            continue
        if cur is None or not line.strip() or line.strip().startswith("//"):
            continue
        kv = re.match(r"^(\w+):\s?(.*)$", line)
        if cur["type"] not in ("text", "movies", "persons") and kv:
            cur["fields"][kv.group(1).lower()] = kv.group(2).strip()
            cur["last"] = kv.group(1).lower()
        elif cur["type"] not in ("text", "movies", "persons") and cur.get("last"):
            cur["fields"][cur["last"]] += " " + line.strip()
        else:
            cur["body"].append(line.strip())
    return sections


def ids(body):
    return [int(x) for x in re.findall(r"\d+", " ".join(body))]


def chapter(path, write_images=False):
    era, page = map(int, re.findall(r"main_(\d+)/page_(\d+)\.md$", path)[0])
    errors, notes = [], []
    secs = parse(path)
    content, sumario, movies, persons = [], None, [], []

    def need(f, keys, kind):
        for k in keys:
            if not f.get(k):
                errors.append(f"[{kind}] falta '{k}'")

    for s in secs:
        t, f = s["type"], s["fields"]
        if t == "sumario":
            need(f, ["title", "description", "image"], t)
            img = fetch_image(parse_image(f.get("image", ""), errors), errors, write_images)
            sumario = {"id": page, "title": f.get("title", ""), "description": render(f.get("description", ""), errors),
                       "image": local_image(img, 350)}
        elif t == "text":
            content.append({"type": "text", "content_text": render(" ".join(s["body"]), errors)})
        elif t in ("video", "essay"):
            need(f, ["youtube", "title"] + (["text", "source"] if t == "video" else ["description", "channel"]), t)
            yt = youtube(f.get("youtube", ""))
            if "__error__" in yt:
                errors.append(f"[{t}] vídeo {f.get('youtube')} indisponível/sem incorporação ({yt['__error__']})")
            else:
                notes.append(f"vídeo {f.get('youtube')}: \"{yt.get('title')}\" — {yt.get('author_name')}")
            if t == "video":
                content.append({"type": "video", "video_id": f.get("youtube"), "information": {
                    "contentTitle": render(f.get("title", ""), errors), "contentText": render(f.get("text", ""), errors),
                    "source": f.get("source", "")}})
            else:
                content.append({"type": "essay", "video_id": f.get("youtube"), "title": f.get("title"),
                                "description": render(f.get("description", ""), errors), "channel": f.get("channel")})
        elif t == "quote":
            need(f, ["quote", "author"], t)
            content.append({"type": "quote", "quote": {"quote": f.get("quote"), "author": f.get("author")},
                            "quote_mark_color": "md_red_500"})
        elif t == "block":
            need(f, ["title", "image", "text"], t)
            img = fetch_image(parse_image(f.get("image", ""), errors), errors, write_images)
            block = {"type": "block_special", "title": f.get("title"), "image": local_image(img),
                     "description": render(f.get("text", ""), errors)}
            if f.get("link"):
                url, _, button = f["link"].partition("|")
                block["click"] = {"screen": "link_online", "button_text": button.strip() or "Saiba mais",
                                  "parameters": [{"key": "LINK", "value": url.strip()}]}
            content.append(block)
        elif t == "image":
            need(f, ["image", "title", "text", "source"], t)
            img = fetch_image(parse_image(f.get("image", ""), errors), errors, write_images)
            content.append({"type": "image", "image": local_image(img), "information": {
                "contentTitle": render(f.get("title", ""), errors), "contentText": render(f.get("text", ""), errors),
                "source": f.get("source", "")}})
        elif t == "movies":
            movies = ids(s["body"])
        elif t == "persons":
            persons = ids(s["body"])
        else:
            errors.append(f"seção desconhecida: # {t}")

    if not sumario:
        errors.append("falta # sumario")
    if len(movies) < 4:
        errors.append("lista '# movies' (Vale a conferida) com menos de 4 filmes")

    movie_items = []
    for mid in movies:
        m = movie(mid, "pt-BR")
        if "__error__" in m:
            errors.append(f"[movies] filme {mid} não existe")
            continue
        movie_items.append({k: m.get(k) for k in ("poster_path", "popularity", "vote_count", "video", "id", "adult",
                                                    "backdrop_path", "original_language", "original_title", "title",
                                                    "vote_average", "overview", "release_date")}
                           | {"media_type": "movie", "genre_ids": [g["id"] for g in m.get("genres", [])]})
    person_items = []
    for pid in persons:
        p = person(pid)
        if "__error__" in p:
            errors.append(f"[persons] pessoa {pid} não existe")
            continue
        person_items.append({"id": pid, "profile_path": p.get("profile_path"), "name": p.get("name")})

    if movie_items:
        content.append({"type": "movie_list", "movies": movie_items})
    if person_items:
        content.append({"type": "person_list", "persons": person_items})
    if era in TIMELINE_TEXT and sumario:
        title, index = TIMELINE_TEXT[era]
        content.append({"type": "link_screen", "subtitle": "Os acontecimentos que marcaram o mundo", "title": title,
                        "click": {"screen": "timeline", "parameters": [{"key": "VIEWPAGER_INDEX", "value": index}]},
                        "image": local_image("img_timeline_poster"),
                        "description": TIMELINE_LINK_DESCRIPTION.get(era, "")})
    return {"era": era, "page": page, "sumario": sumario, "page_json": {"number": page, "content_list": content},
            "errors": errors, "notes": notes, "movies": movie_items, "persons": person_items}


TIMELINE_LINK_DESCRIPTION = {
    7: "A animação além da Disney, o cinema estrangeiro conquistando o Oscar, os super-heróis, o terror e o streaming. Clique aqui e veja de forma cronológica os principais acontecimentos do cinema de 2010 a 2019.",
    8: "A pandemia fecha as salas, o streaming se funde, Hollywood para em greve e o cinema do mundo inteiro chega ao topo. Clique aqui e veja de forma cronológica os principais acontecimentos do cinema de 2020 até hoje.",
}


def plain(html):
    t = re.sub(r"<br\s*/?>", "\n", html)
    return re.sub(r"<[^>]+>", "", t)


def report(ch):
    print(f"=== main_{ch['era']} / página {ch['page']}: {ch['sumario']['title'] if ch['sumario'] else '?'}")
    for c in ch["page_json"]["content_list"]:
        t = c["type"]
        if t == "text":
            print("\n" + plain(c["content_text"]))
        elif t in ("video", "image"):
            i = c["information"]
            print(f"\n[{t.upper()}] {plain(i['contentTitle'])} — {plain(i['contentText'])} ({plain(i['source'])})")
        elif t == "essay":
            print(f"\n[ENSAIO] {c['title']} — {plain(c['description'])}")
        elif t == "quote":
            print(f"\n[CITAÇÃO] \"{c['quote']['quote']}\" — {c['quote']['author']}")
        elif t == "block_special":
            print(f"\n[TEXTO INTERNO: {c['title']}]\n{plain(c['description'])}")
    print("\n[VALE A CONFERIDA] " + "; ".join(f"{m['title']} ({(m['release_date'] or '')[:4]})" for m in ch["movies"]))
    print("[PESSOAS] " + "; ".join(p["name"] for p in ch["persons"]))
    for n in ch["notes"]:
        print("  · " + n)
    for e in ch["errors"]:
        print("  ✗ ERRO: " + e)
    print()


def main():
    args = sys.argv[1:]
    if not args or args[0] not in ("check", "build"):
        print(__doc__)
        return 1
    if args[0] == "check":
        bad = 0
        for p in args[1:]:
            ch = chapter(os.path.abspath(p))
            report(ch)
            bad += len(ch["errors"])
        return 1 if bad else 0

    era_filter = int(args[args.index("--era") + 1]) if "--era" in args else None
    files = sorted(
        (os.path.join(dp, f) for dp, _, fs in os.walk(os.path.join(HERE, "pt")) for f in fs if f.endswith(".md")))
    bad = 0
    sumarios = {}
    for p in files:
        ch = chapter(p, write_images=True)
        if era_filter and ch["era"] != era_filter:
            continue
        if ch["errors"]:
            report(ch)
            bad += 1
            continue
        out_dir = os.path.join(ASSETS, "pages", f"main_{ch['era']}")
        os.makedirs(out_dir, exist_ok=True)
        json.dump(ch["page_json"], open(os.path.join(out_dir, f"main_{ch['era']}_page_{ch['page']}.json"), "w"),
                  ensure_ascii=False, indent=2)
        sumarios.setdefault(ch["era"], []).append(ch["sumario"])
        print(f"ok  main_{ch['era']}_page_{ch['page']}  {ch['sumario']['title']}")
    for era, items in sumarios.items():
        path = os.path.join(ASSETS, "history_sumarios", f"hmt_sumarios_{era}.json")
        current = json.load(open(path)) if os.path.exists(path) else []
        by_id = {s["id"]: s for s in current}
        for s in items:
            by_id[s["id"]] = s
        json.dump([by_id[k] for k in sorted(by_id)], open(path, "w"), ensure_ascii=False, indent=1)
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main())
