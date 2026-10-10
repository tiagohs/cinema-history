"""
Biblioteca do pipeline de tradução do conteúdo (assets/local).

Regras centrais:
  * O português (assets/local/pt) é a ÚNICA fonte de verdade. en/es são gerados.
  * Só strings em chaves traduzíveis são extraídas; ids, urls, enums, cores etc. nunca passam pelo tradutor.
  * Tags HTML dentro do texto viram marcadores (<t1>…</t1>, <t2/>). O href dos links internos
    (https://_{...}) fica guardado no texto em português e é restaurado na reinserção.
  * Objetos que são "snapshots" do TMDB (filmes/pessoas embutidos) são ignorados inteiros.
"""
from __future__ import annotations

import hashlib
import json
import os
import re
from dataclasses import dataclass, field
from typing import Any, Iterator

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(ROOT, "android", "app", "src", "main", "assets", "local")
SOURCE_LANG = "pt"
TARGET_LANGS = ("en", "es")
HERE = os.path.dirname(os.path.abspath(__file__))
TRANSLATIONS_DIR = os.path.join(HERE, "translations")
TMDB_CACHE = os.path.join(HERE, "tmdb", "titles.json")

# --------------------------------------------------------------------------------------
# Regras de classificação
# --------------------------------------------------------------------------------------

# Chaves cujo valor (string) é texto para o leitor.
TRANSLATABLE_KEYS = {
    "content_text", "description", "content_title", "title", "subtitle", "quote", "author",
    "content_description", "credits", "content_credits", "button_text", "buttonText",
    "page_title", "next", "previous", "awards", "content", "text", "name", "country",
    "presented_by", "department", "contentText", "contentTitle", "source",
    "years",            # períodos do perfil de diretores ("Desde 2015", "n. 1969")
}

# Subárvores que nunca são traduzidas (dados de terceiros ou nomes próprios).
SKIP_KEYS = {
    "review_results",   # críticas de sites (pt/en): exibidas conforme o idioma, não traduzidas
    "watchOn",          # lojas/streaming (dados regionais)
    "social_list", "style", "animation", "parameters", "twitter_html",
}

# Campos que identificam um objeto do TMDB embutido (filme/pessoa/crédito): ignorado inteiro.
TMDB_MARKERS = {
    "poster_path", "backdrop_path", "profile_path", "vote_average", "vote_count", "credit_id",
    "release_date", "original_language", "popularity", "imdb_id", "known_for_department",
}

# Chaves cujo valor é um nome de pessoa (mantido como está).
PERSON_NAME_KEYS = {"director", "custom_name", "reviewer", "reviewer_name"}


@dataclass
class Occurrence:
    file: str           # caminho relativo a assets/local/<lang>
    pointer: str        # JSON pointer (RFC 6901)
    key: str


@dataclass
class Special:
    """Valor resolvido automaticamente (sem tradução livre), ex.: título oficial de filme no TMDB."""
    file: str
    pointer: str
    kind: str           # "movie_title"
    tmdb_id: int


@dataclass
class WalkResult:
    texts: list[tuple[Occurrence, str]] = field(default_factory=list)
    specials: list[Special] = field(default_factory=list)


def _ptr(parts: list[Any]) -> str:
    return "/" + "/".join(str(p).replace("~", "~0").replace("/", "~1") for p in parts) if parts else ""


def is_tmdb_object(d: dict) -> bool:
    return bool(TMDB_MARKERS.intersection(d.keys()))


def walk(doc: Any, file: str) -> WalkResult:
    out = WalkResult()

    def rec(node: Any, parts: list[Any], parent_key: str | None):
        if isinstance(node, dict):
            # Destaque do índice dos prêmios (awards/nominees/<id>/index.json): só o título do filme.
            if parent_key == "highlight" and isinstance(node.get("id"), int) and "name" in node:
                if node.get("type", "movie") == "movie":
                    out.specials.append(Special(file, _ptr(parts + ["name"]), "movie_title", node["id"]))
                if isinstance(node.get("category"), str) and node["category"].strip():
                    out.texts.append((Occurrence(file, _ptr(parts + ["category"]), "category"), node["category"]))
                return
            # Item de lista de indicados: {type: movie|person, id, name, ...}
            # (antes do teste de objeto do TMDB: vencedores levam "backdrop_path")
            if parent_key in ("nominee_list",) or (parent_key == "movie" and "id" in node and "name" in node):
                if node.get("type", "movie") == "movie" and isinstance(node.get("id"), int) and "name" in node:
                    out.specials.append(Special(file, _ptr(parts + ["name"]), "movie_title", node["id"]))
                for k, v in node.items():
                    if k == "department" and isinstance(v, str) and v.strip():
                        out.texts.append((Occurrence(file, _ptr(parts + [k]), k), v))
                    elif k == "movie" and isinstance(v, dict):
                        rec(v, parts + [k], k)
                return
            if is_tmdb_object(node):
                return
            # Filme de specials/movies: {id, title, original_title, ...}
            is_special_movie = file.startswith("specials/movies") and parent_key == "movies"
            if isinstance(node.get("id"), int) and "title" in node and ("original_title" in node or is_special_movie):
                out.specials.append(Special(file, _ptr(parts + ["title"]), "movie_title", node["id"]))
                for k, v in node.items():
                    if k not in ("title", "original_title"):
                        rec(v, parts + [k], k)
                return
            # Vídeos de specials/persons: nome do vídeo é título original; "type" é rótulo exibido.
            if parent_key == "videos" and "key" in node:
                v = node.get("type")
                if isinstance(v, str) and v.strip():
                    out.texts.append((Occurrence(file, _ptr(parts + ["type"]), "video_type"), v))
                return
            for k, v in node.items():
                if k in SKIP_KEYS or k in PERSON_NAME_KEYS:
                    continue
                if isinstance(v, str):
                    if k in TRANSLATABLE_KEYS and v.strip() and not _looks_untranslatable(v):
                        out.texts.append((Occurrence(file, _ptr(parts + [k]), k), v))
                else:
                    rec(v, parts + [k], k)
        elif isinstance(node, list):
            for i, v in enumerate(node):
                rec(v, parts + [i], parent_key)

    rec(doc, [], None)
    return out


_URL_RE = re.compile(r"^(https?://|gs://|www\.)\S+$")


def _looks_untranslatable(v: str) -> bool:
    s = v.strip()
    if _URL_RE.match(s):
        return True
    if re.fullmatch(r"[\d\s.,:/\-–()%+]+", s):        # números, datas, anos
        return True
    return False


# --------------------------------------------------------------------------------------
# Marcadores de tags HTML
# --------------------------------------------------------------------------------------

TAG_RE = re.compile(r"<\s*(/?)\s*([a-zA-Z][a-zA-Z0-9]*)\b([^>]*?)(/?)\s*>")
VOID_TAGS = {"br", "hr", "img"}
PAIR_SEP = "\u0000"  # separa abertura|fechamento de um par de tags


def mask(text: str) -> tuple[str, list[str]]:
    """Troca cada tag HTML por um marcador. Retorna (texto_mascarado, lista_de_tags_originais).

    Tags de abertura e fechamento correspondentes recebem o MESMO número: <t3>…</t3>.
    Tags vazias viram <t4/>. tags[n-1] guarda o texto original da tag (ou do par "abre|fecha").
    """
    tags: list[str] = []
    pieces: list[str] = []
    pos = 0
    # Primeiro passe: casar aberturas e fechamentos
    matches = list(TAG_RE.finditer(text))
    pair_of: dict[int, int] = {}
    st: list[tuple[str, int]] = []
    for idx, m in enumerate(matches):
        closing, name, selfclose = m.group(1) == "/", m.group(2).lower(), m.group(4) == "/"
        if closing:
            for j in range(len(st) - 1, -1, -1):
                if st[j][0] == name:
                    pair_of[st[j][1]] = idx
                    pair_of[idx] = st[j][1]
                    del st[j:]
                    break
        elif not selfclose and name not in VOID_TAGS:
            st.append((name, idx))

    number_of: dict[int, int] = {}
    for idx, m in enumerate(matches):
        pieces.append(text[pos:m.start()])
        pos = m.end()
        closing = m.group(1) == "/"
        if idx in pair_of and not closing:
            tags.append(m.group(0) + PAIR_SEP + matches[pair_of[idx]].group(0))
            number_of[idx] = number_of[pair_of[idx]] = len(tags)
            pieces.append(f"<t{len(tags)}>")
        elif idx in pair_of and closing:
            pieces.append(f"</t{number_of[idx]}>")
        else:
            tags.append(m.group(0))
            pieces.append(f"<t{len(tags)}/>")
    pieces.append(text[pos:])
    return "".join(pieces), tags


MARK_RE = re.compile(r"<(/?)t(\d+)(/?)>")


def unmask(masked: str, tags: list[str]) -> str:
    def repl(m: re.Match) -> str:
        n = int(m.group(2))
        if n < 1 or n > len(tags):
            raise ValueError(f"marcador inexistente t{n}")
        open_tag, _, close_tag = tags[n - 1].partition(PAIR_SEP)
        return close_tag if m.group(1) == "/" else open_tag
    return MARK_RE.sub(repl, masked)


def marker_signature(masked: str) -> list[str]:
    """Lista ordenada dos marcadores, usada para comparar origem e tradução."""
    return sorted(m.group(0) for m in MARK_RE.finditer(masked))


def check_markers(source_masked: str, translated_masked: str) -> list[str]:
    errors = []
    if marker_signature(source_masked) != marker_signature(translated_masked):
        errors.append("marcadores diferentes da origem")
    # Abertura antes do fechamento
    seen_open: set[str] = set()
    for m in MARK_RE.finditer(translated_masked):
        if m.group(3) == "/":
            continue
        if m.group(1) == "/":
            if m.group(2) not in seen_open:
                errors.append(f"</t{m.group(2)}> antes de <t{m.group(2)}>")
        else:
            seen_open.add(m.group(2))
    if source_masked.count("\n") != translated_masked.count("\n"):
        errors.append("quantidade de quebras de linha (\\n) diferente")
    return errors


def entry_id(masked_source: str) -> str:
    return hashlib.sha1(masked_source.encode("utf-8")).hexdigest()[:12]


# --------------------------------------------------------------------------------------
# Arquivos
# --------------------------------------------------------------------------------------

def lang_dir(lang: str) -> str:
    return os.path.join(ASSETS, lang)


def iter_json_files(lang: str = SOURCE_LANG) -> Iterator[str]:
    base = lang_dir(lang)
    for dp, _, fs in os.walk(base):
        for f in sorted(fs):
            if f.endswith(".json"):
                yield os.path.relpath(os.path.join(dp, f), base)


def load_json(path: str) -> Any:
    with open(path, encoding="utf-8") as fh:
        return json.load(fh)


def dump_json(path: str, data: Any) -> None:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    # Grava num temporário e troca no fim: com disco cheio, o original fica intacto.
    tmp = path + ".tmp"
    with open(tmp, "w", encoding="utf-8") as fh:
        json.dump(data, fh, ensure_ascii=False, indent=2)
        fh.write("\n")
    os.replace(tmp, path)


def resolve_pointer(doc: Any, pointer: str) -> tuple[Any, Any]:
    """Retorna (container, chave) do JSON pointer."""
    parts = [p.replace("~1", "/").replace("~0", "~") for p in pointer.split("/")[1:]]
    node = doc
    for p in parts[:-1]:
        node = node[int(p)] if isinstance(node, list) else node[p]
    last = parts[-1]
    return node, (int(last) if isinstance(node, list) else last)


def file_group(rel: str) -> str:
    """Grupo do arquivo, para lotes e relatórios de cobertura."""
    if rel.startswith("pages/"):
        return "pages/" + rel.split("/")[1]
    if "/" in rel:
        return rel.split("/")[0]
    return rel.removesuffix(".json")


# --------------------------------------------------------------------------------------
# Catálogos de tradução: translations/<lang>.json
#   { "<id>": {"src": "<texto mascarado em pt>", "t": "<tradução mascarada>"} }
# --------------------------------------------------------------------------------------

def load_translations(lang: str) -> dict[str, dict]:
    path = os.path.join(TRANSLATIONS_DIR, f"{lang}.json")
    if not os.path.exists(path):
        return {}
    return load_json(path)


def save_translations(lang: str, data: dict[str, dict]) -> None:
    dump_json(os.path.join(TRANSLATIONS_DIR, f"{lang}.json"), dict(sorted(data.items())))
