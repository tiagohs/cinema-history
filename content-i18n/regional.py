"""
Adaptação regional do conteúdo traduzido (en/es).

Além do texto, alguns dados do conteúdo em pt são específicos do Brasil:
  * "Onde assistir" (specials/movies -> watchOn): lojas e streamings brasileiros.
    Para cada idioma, a lista é refeita com os provedores que o TMDB/JustWatch confirmam
    no país de referência (en -> EUA, es -> México). Quando o pt já tem um link direto
    adaptável (iTunes, Google Play, Netflix, YouTube...), ele é ajustado para o país;
    caso contrário o item aponta para a página "onde assistir" do TMDB naquele país.
  * Críticas (review_results): só as críticas em inglês são mantidas fora do pt.
  * Links para páginas em português (livros na Amazon BR, artigos do glossário, manifesto...):
    substituídos pelo equivalente do idioma conforme regional/<idioma>.json.

Tudo usa apenas caches versionados (tmdb/watch.json), então apply/validate são determinísticos.
"""
from __future__ import annotations

import copy
import json
import os
import re
import time
import urllib.parse
import urllib.request

import i18n_lib as L

REGION = {"en": "US", "es": "MX", "pt": "BR"}
UI_LANG = {"en": "en", "es": "es-419", "pt": "pt-BR"}
WATCH_CACHE = os.path.join(L.HERE, "tmdb", "watch.json")
REVIEW_LANGUAGES = {"en": {"en-US"}, "es": {"en-US"}}

# Nome do provedor no TMDB -> NetworkType do app (entities/enums/NetworkType.kt)
PROVIDER_TYPES = {
    "Netflix": "netflix",
    "Amazon Prime Video": "prime_video",
    "Amazon Video": "amazon_video",
    "Apple TV": "apple_tv",
    "Apple TV Store": "apple_tv",
    "Google Play Movies": "play_store",
    "YouTube": "youtube",
    "Max": "hbo_max",
    "HBO Max": "hbo_max",
    "MUBI": "mubi",
    "Kanopy": "kanopy",
    "Criterion Channel": "criterionchannel",
    "The Criterion Channel": "criterionchannel",
    "Claro video": "claro_video",
    "Dailymotion": "dailymotion",
    # Brasil
    "Globoplay": "globo_play",
    "Claro tv+": "claro_video",
    "Looke": "looke",
    "Looke Amazon Channel": "looke",
    "Telecine": "telecine",
    "Telecine Amazon Channel": "telecine",
    "Vivo Play": "vivo_play",
    "NOW": "now",
    "UOL Play": "uol_play",
    "Max Amazon Channel": "hbo_max",
    "Fandor": "fandor",
    # variantes com anúncios / canais
    "Netflix basic with Ads": "netflix",
    "Netflix Standard with Ads": "netflix",
    "Amazon Prime Video with Ads": "prime_video",
    "YouTube Free": "youtube",
    "MUBI Amazon Channel": "mubi",
    "HBO Max Amazon Channel": "hbo_max",
    "Criterion Channel Amazon Channel": "criterionchannel",
}
MORE_OPTIONS = "more_options"
CATEGORY_ORDER = ("flatrate", "free", "ads", "rent", "buy")


# --------------------------------------------------------------------------------------
# Cache de provedores (TMDB watch/providers)
# --------------------------------------------------------------------------------------

def load_watch_cache() -> dict:
    return L.load_json(WATCH_CACHE) if os.path.exists(WATCH_CACHE) else {}


def watch_movie_ids() -> list[int]:
    doc = L.load_json(os.path.join(L.lang_dir(L.SOURCE_LANG), "specials", "movies.json"))
    return sorted({m["id"] for g in doc for m in g.get("movies", []) if m.get("watchOn") is not None})


def fetch_watch(key: str, movie_id: int) -> dict | None:
    url = f"https://api.themoviedb.org/3/movie/{movie_id}/watch/providers?" + urllib.parse.urlencode({"api_key": key})
    for attempt in range(4):
        try:
            with urllib.request.urlopen(url, timeout=30) as r:
                data = json.load(r).get("results", {})
            break
        except urllib.error.HTTPError as e:
            if e.code == 404:
                return {}
            time.sleep(2 * (attempt + 1))
        except Exception:
            time.sleep(2 * (attempt + 1))
    else:
        return None
    out = {}
    for region in set(REGION.values()):
        info = data.get(region) or {}
        types: list[str] = []
        other = False
        for cat in CATEGORY_ORDER:
            for p in info.get(cat, []) or []:
                t = PROVIDER_TYPES.get(p.get("provider_name"))
                if t and t not in types:
                    types.append(t)
                elif not t:
                    other = True
        out[region] = {"link": info.get("link"), "types": types, "other": other}
    return out


# --------------------------------------------------------------------------------------
# Links por idioma
# --------------------------------------------------------------------------------------

def load_links(lang: str) -> dict[str, dict]:
    path = os.path.join(L.HERE, "regional", f"{lang}.json")
    return L.load_json(path).get("links", {}) if os.path.exists(path) else {}


def _apply_links(node, links: dict[str, dict]) -> None:
    if isinstance(node, dict):
        # Item com um link mapeado (o URL pode estar em qualquer campo, ex.: subtitle trocado com link)
        hit = None
        for v in node.values():
            if isinstance(v, str) and v.strip() in links:
                hit = links[v.strip()]
                break
        if hit:
            for k, v in list(node.items()):
                if isinstance(v, str) and v.strip() in links:
                    node[k] = hit["link"]
            if "link" in node and isinstance(node["link"], str) and not node["link"].startswith("http"):
                node["link"] = hit["link"]
            for field in ("title", "subtitle"):
                if field in hit and field in node:
                    node[field] = hit[field]
        # Botão de um click cujo parâmetro LINK foi mapeado
        params = node.get("parameters")
        if isinstance(params, list) and "button_text" in node:
            for p in params:
                if isinstance(p, dict) and isinstance(p.get("value"), str) and p["value"].strip() in links:
                    override = links[p["value"].strip()]
                    p["value"] = override["link"]
                    if "button_text" in override:
                        node["button_text"] = override["button_text"]
        for v in node.values():
            _apply_links(v, links)
    elif isinstance(node, list):
        for v in node:
            _apply_links(v, links)


# --------------------------------------------------------------------------------------
# Onde assistir
# --------------------------------------------------------------------------------------

def _adapt_link(network: str, link: str, region: str, lang: str) -> str | None:
    """Link direto ajustado ao país, ou None se o link do pt não serve fora do Brasil."""
    r = region.lower()
    if network == "apple_tv" and re.search(r"(itunes|tv)\.apple\.com/[a-z]{2}/", link):
        return re.sub(r"((?:itunes|tv)\.apple\.com/)[a-z]{2}/", rf"\g<1>{r}/", link)
    if network == "play_store" and "play.google.com" in link:
        parts = urllib.parse.urlsplit(link)
        q = dict(urllib.parse.parse_qsl(parts.query))
        q["gl"], q["hl"] = region, UI_LANG[lang]
        return urllib.parse.urlunsplit(parts._replace(query=urllib.parse.urlencode(q)))
    if network == "netflix" and "netflix.com" in link:
        return re.sub(r"netflix\.com/[a-z]{2}(-[a-z]{2})?/", "netflix.com/", link)
    if network in ("youtube", "mubi", "kanopy", "criterionchannel", "dailymotion"):
        return link if ".br" not in urllib.parse.urlsplit(link).netloc else None
    if network == "prime_video" and ("primevideo.com" in link or (region == "US" and "amazon.com/" in link)):
        return link
    if network == "amazon_video" and region == "US" and "amazon.com/" in link:
        return link
    return None


def regional_watch_on(movie_id: int, pt_watch_on: list, lang: str, cache: dict) -> list:
    region = REGION[lang]
    info = (cache.get(str(movie_id)) or {}).get(region)
    if not info:
        return []
    existing = {}
    for w in pt_watch_on or []:
        existing.setdefault(w.get("type"), w.get("link") or "")
    result = []
    for network in info["types"]:
        link = _adapt_link(network, existing[network], region, lang) if network in existing else None
        link = link or info.get("link")
        if link:
            result.append({"type": network, "link": link})
    # Serviços que o app não exibe individualmente: um item "Mais opções" com a página do país
    if info.get("other") and info.get("link"):
        result.append({"type": MORE_OPTIONS, "link": info["link"]})
    return result


# --------------------------------------------------------------------------------------
# Aplicação
# --------------------------------------------------------------------------------------

def regionalize(doc, rel: str, lang: str, links: dict, watch_cache: dict):
    """Aplica as adaptações regionais ao documento (já traduzido). Retorna o documento."""
    if lang == L.SOURCE_LANG:
        return doc
    _apply_links(doc, links)
    if rel == os.path.join("specials", "movies.json"):
        allowed_reviews = REVIEW_LANGUAGES.get(lang, set())
        for group in doc:
            for movie in group.get("movies", []):
                if "watchOn" in movie:
                    movie["watchOn"] = regional_watch_on(movie["id"], movie["watchOn"], lang, watch_cache)
                if "review_results" in movie:
                    movie["review_results"] = [r for r in movie["review_results"] if r.get("language") in allowed_reviews]
    return doc


def expected_structure(src_doc, rel: str, lang: str, links: dict, watch_cache: dict):
    """Versão do pt com as adaptações regionais: base de comparação do validate."""
    return regionalize(copy.deepcopy(src_doc), rel, lang, links, watch_cache)
