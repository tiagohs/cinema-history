#!/usr/bin/env python3
"""Gera os roteiros de narração (texto pronto para TTS) dos capítulos do Cinema History.

Lê:  android/app/src/main/assets/local/<idioma>/pages/main_<era>/main_<era>_page_<n>.json
     .../history_sumarios/hmt_sumarios_<era>.json  (título e resumo do capítulo)
     .../maintopics.json                           (nome da era)
Gera: content-src/audio/roteiros/<idioma>/main_<era>/page_<n>.json  (+ .txt legível)
      content-src/audio/roteiros/resumo.csv
Só biblioteca padrão. Uso: python3 content-src/audio/scripts.py [--langs pt,en,es] [--era 1] [--page 3]
"""
import argparse
import csv
import json
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import normalize as N  # noqa: E402

ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
ASSETS = os.path.join(ROOT, "android", "app", "src", "main", "assets", "local")
OUT = os.path.join(HERE, "roteiros")

LANGS = ["pt", "en", "es"]
CHARS_PER_MIN = {"pt": 900, "en": 1000, "es": 950}
MAX_TRACK_CHARS = 2500   # bloco maior que isso é quebrado em fim de parágrafo
MIN_TRACK_CHARS = 700    # bloco menor que isso é juntado ao anterior (se couber), para não ter faixas de 30 s

# elementos ignorados (visuais / listas). Eles só servem de "fronteira" entre blocos de texto.
VISUAL = {"video", "image", "gif", "essay", "link_screen", "person_list", "slide", "twitter"}
MOVIE_LISTS = {"movie_list", "movie_list_special"}

L10N = {
    "pt": {"chapter": "Capítulo", "open": "Abertura", "more": "Saiba mais", "part": "Parte",
           "watch_title": "Vale a conferida", "said": "disse", "in": "em",
           "watch_text": "Vale a conferida: os filmes deste capítulo são {list}.",
           "and": "e"},
    "en": {"chapter": "Chapter", "open": "Opening", "more": "Learn more", "part": "Part",
           "watch_title": "Worth watching", "said": "said", "in": "in",
           "watch_text": "Worth watching: the films from this chapter are {list}.",
           "and": "and"},
    "es": {"chapter": "Capítulo", "open": "Apertura", "more": "Para saber más", "part": "Parte",
           "watch_title": "Vale la pena ver", "said": "dijo", "in": "en",
           "watch_text": "Vale la pena ver: las películas de este capítulo son {list}.",
           "and": "y"},
}


def load(path):
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def chars_of(text):
    """Caracteres cobrados pelo TTS: conta o nome original no lugar das marcas [[pron]]."""
    return len(N.render_pron(text, "original"))


class Chapter:
    def __init__(self, lang, era, page):
        self.lang, self.era, self.page = lang, era, page
        self.tracks = []
        self.cur = None  # faixa em construção: {"kind","title","segs":[(speaker, original)], "strong": [...]}

    # --- faixas -----------------------------------------------------------------
    def new_track(self, kind, title=None):
        self.cur = {"kind": kind, "title": title, "segs": []}
        self.tracks.append(self.cur)
        return self.cur

    def flush(self):
        self.cur = None

    def add_text(self, original, hints=(), src=-1):
        if self.cur is None or self.cur["kind"] != "body":
            self.new_track("body")
        self.cur["segs"].append(("narrador", original, list(hints), src))

    def add_quote(self, quote, author, src=-1):
        if self.cur is None or self.cur["kind"] != "body":
            self.new_track("body")
        L = L10N[self.lang]
        self.cur["segs"].append(("citacao", quote, [], src))
        if author:
            a = author.strip()
            a = re.sub(r"\s+-\s+", f", {L['in']} ", a)          # "Georges Méliès - Hugo"
            a = re.sub(r"\s*\(([^()]*)\)\s*$", r", \1", a)        # "Walter Salles (discurso, 2025)"
            # o "…" marca a atribuição (é removido na normalização)
            self.cur["segs"].append(("narrador", f"…{L['said']} {a}.", [], src))


def _hints(raw):
    """Candidatos a título da faixa: textos em <strong>, depois links (pessoa/filme)."""
    strong = [N.clean_html(x) for x in re.findall(r"<strong>(.*?)</(?:strong)?>", raw, re.S)]
    links = [N.clean_html(x) for x in re.findall(r"<a\b[^>]*>(.*?)</a>", N.drop_br_titles(raw), re.S)]
    return [("s", x) for x in strong if 3 <= len(x) <= 60] + [("a", x) for x in links if 3 <= len(x) <= 60]


_SPOKEN_LEN = {}


def slen(text, lang):
    """Tamanho do texto já falado (números por extenso aumentam o texto)."""
    k = (text, lang)
    if k not in _SPOKEN_LEN:
        _SPOKEN_LEN[k] = chars_of(N.speak(text.lstrip("…"), lang))
    return _SPOKEN_LEN[k]


def _split_long(segs, limit, lang):
    """Quebra uma lista de segmentos em grupos de até `limit` caracteres, em fim de parágrafo.
    Citação + atribuição ficam juntas."""
    groups, cur, size = [], [], 0
    # parágrafo sozinho maior que o limite: quebra em fim de frase (caso raro)
    expanded = []
    for sp, txt, h, src in segs:
        if slen(txt, lang) <= limit or sp != "narrador":
            expanded.append((sp, txt, h, src))
            continue
        sentences = re.split(r"(?<=[.!?])\s+(?=[A-ZÀ-Ý\"'“])", txt)
        buf = ""
        for snt in sentences:
            if buf and slen(buf + " " + snt, lang) > limit * 0.6:
                expanded.append((sp, buf, h, src)); h = []; buf = snt
            else:
                buf = (buf + " " + snt).strip()
        if buf:
            expanded.append((sp, buf, h, src))
    segs = expanded
    i = 0
    units = []
    while i < len(segs):
        u = [segs[i]]
        if segs[i][0] == "citacao" and i + 1 < len(segs) and segs[i + 1][1].startswith("…"):
            u.append(segs[i + 1])
            i += 1
        units.append(u)
        i += 1
    for u in units:
        n = sum(slen(s[1], lang) for s in u)
        if cur and size + n > limit:
            groups.append(cur)
            cur, size = [], 0
        cur.extend(u)
        size += n
    if cur:
        groups.append(cur)
    # evita uma última parte muito pequena
    if len(groups) > 1 and sum(slen(s[1], lang) for s in groups[-1]) < MIN_TRACK_CHARS:
        if sum(slen(s[1], lang) for s in groups[-2] + groups[-1]) <= limit * 1.1:
            groups[-2].extend(groups.pop())
    return groups


def build_chapter(lang, era, page, era_info, summary):
    L = L10N[lang]
    data = load(os.path.join(ASSETS, lang, "pages", f"main_{era}", f"main_{era}_page_{page}.json"))
    ch = Chapter(lang, era, page)

    # 00 Abertura
    era_title, era_sub = era_info.get("title", ""), era_info.get("subtitle", "")
    title = summary.get("title", "").strip()
    desc = N.clean_html(summary.get("description", ""))
    t = ch.new_track("open", L["open"])
    opening = f"{era_sub}: {era_title}. {L['chapter']} {page}: {title}."
    t["segs"].append(("narrador", N.clean_html(opening), [], -1))
    if desc:
        t["segs"].append(("narrador", desc, [], -1))
    ch.flush()

    movies = []
    movies_src = None  # índice da primeira movie_list (a faixa "Vale a conferida" aponta para ela)
    for idx, el in enumerate(data.get("content_list", [])):
        typ = el.get("type")
        if typ == "text":
            raw = el.get("content_text") or ""
            paras = N.split_paragraphs(raw)
            for i, p in enumerate(paras):
                ch.add_text(p, _hints(raw) if i == 0 else (), src=idx)
        elif typ == "quote":
            q = el.get("quote") or {}
            qt = N.clean_html(q.get("quote") or "")
            if qt:
                ch.add_quote(qt, N.clean_html(q.get("author") or ""), src=idx)
        elif typ == "block_special":
            ch.flush()
            btitle = N.clean_html(el.get("title") or "")
            paras = N.split_paragraphs(el.get("description") or "")
            if paras:
                tr = ch.new_track("more", f"{L['more']}: {btitle}")
                tr["segs"].append(("narrador", f"{L['more']}: {btitle}.", [], idx))
                tr["segs"].extend(("narrador", p, [], idx) for p in paras)
            ch.flush()
        elif typ in MOVIE_LISTS:
            for m in el.get("movies") or []:
                name = (m.get("title") or m.get("name") or m.get("original_title") or "").strip()
                if name and name not in movies:
                    movies.append(name)
                    if movies_src is None:
                        movies_src = idx
        elif typ in VISUAL:
            # fronteira de bloco; uma citação depois de um visual abre o bloco seguinte
            if ch.cur is not None and ch.cur["kind"] == "body" and ch.cur["segs"]:
                ch.flush()
        else:
            ch.flush()

    # pós-processamento: junta blocos pequenos e quebra os grandes
    merged = []
    for tr in ch.tracks:
        size = sum(slen(s[1], lang) for s in tr["segs"])
        prev = merged[-1] if merged else None
        if (tr["kind"] == "body" and prev is not None and prev["kind"] == "body"
                and (size < MIN_TRACK_CHARS or sum(slen(s[1], lang) for s in prev["segs"]) < MIN_TRACK_CHARS)
                and size + sum(slen(s[1], lang) for s in prev["segs"]) <= MAX_TRACK_CHARS):
            prev["segs"].extend(tr["segs"])
            continue
        merged.append(tr)

    final = []
    for tr in merged:
        if tr["kind"] == "open":
            final.append(tr)
            continue
        groups = _split_long(tr["segs"], MAX_TRACK_CHARS, lang)
        for gi, g in enumerate(groups):
            ttl = tr["title"]
            if tr["kind"] == "more" and len(groups) > 1:
                ttl = f"{tr['title']} ({gi + 1})"
            final.append({"kind": tr["kind"], "title": ttl, "segs": g})

    if movies:
        lst = movies[0] if len(movies) == 1 else ", ".join(movies[:-1]) + f" {L['and']} " + movies[-1]
        final.append({"kind": "watch", "title": L["watch_title"],
                      "segs": [("narrador", L["watch_text"].format(list=lst), [],
                                movies_src if movies_src is not None else -1)]})

    # títulos das faixas de texto
    used = set()
    body_n = 0
    for tr in final:
        if tr["kind"] != "body":
            continue
        body_n += 1
        hints = [h for s in tr["segs"] for h in s[2]]
        hints = [h for h in hints if h[0] == "s"] + [h for h in hints if h[0] == "a"]
        cand = [h for k, h in hints if h.lower() not in used]
        if cand:
            tr["title"] = cand[0][:1].upper() + cand[0][1:]
            used.add(cand[0].lower())
        else:
            tr["title"] = f"{L['part']} {body_n}"

    # monta a estrutura final com normalização
    tracks_out = []
    for ti, tr in enumerate(final):
        segs = []
        for si, (spk, orig, _h, src) in enumerate(tr["segs"]):
            orig_clean = orig.lstrip("…") if spk == "narrador" else orig
            spoken = N.speak(orig_clean, lang)
            if orig.startswith("…"):
                # atribuição de citação: o narrador fala "…, disse Fulano."
                spoken = spoken[:1].lower() + spoken[1:]
            # source_index: índice do item em content_list de onde o segmento veio (-1 = abertura)
            segs.append({"id": f"{ti:02d}-{si + 1:02d}", "speaker": spk, "source_index": src,
                         "original": orig_clean, "text": spoken, "chars": chars_of(spoken)})
        c = sum(s["chars"] for s in segs)
        tracks_out.append({"id": f"{ti:02d}", "title": tr["title"], "kind": tr["kind"], "segments": segs,
                           "chars": c, "est_minutes": round(c / CHARS_PER_MIN[lang], 2)})
    total = sum(t["chars"] for t in tracks_out)
    return {"lang": lang, "era": era, "page": page, "era_title": f"{era_sub} - {era_title}", "title": title,
            "tracks": tracks_out, "chars": total, "est_minutes": round(total / CHARS_PER_MIN[lang], 2)}


def to_txt(ch):
    head = f"{ch['era_title']} · {ch['title']}  (era {ch['era']}, capítulo {ch['page']}, {ch['lang']})"
    lines = [head, "=" * len(head),
             f"{len(ch['tracks'])} faixas · {ch['chars']} caracteres · ~{ch['est_minutes']:.1f} min", ""]
    for t in ch["tracks"]:
        lines.append(f"## [{t['id']}] {t['title']}  ({t['chars']} car., ~{t['est_minutes']:.1f} min)")
        lines.append("")
        for s in t["segments"]:
            tag = "[CITAÇÃO] " if s["speaker"] == "citacao" else ""
            lines.append(tag + s["text"])
            lines.append("")
    return "\n".join(lines)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--langs", default=",".join(LANGS))
    ap.add_argument("--era", type=int)
    ap.add_argument("--page", type=int)
    args = ap.parse_args()

    rows, totals = [], {}
    for lang in args.langs.split(","):
        eras = {t["id"]: t for t in load(os.path.join(ASSETS, lang, "maintopics.json"))
                if t.get("main_topic_type") == "history_cinema"}
        for era in sorted(eras):
            if args.era and era != args.era:
                continue
            sums = {s["id"]: s for s in load(os.path.join(ASSETS, lang, "history_sumarios", f"hmt_sumarios_{era}.json"))}
            pdir = os.path.join(ASSETS, lang, "pages", f"main_{era}")
            pages = sorted(int(re.search(r"_page_(\d+)\.json$", f).group(1)) for f in os.listdir(pdir) if f.endswith(".json"))
            for page in pages:
                if args.page and page != args.page:
                    continue
                ch = build_chapter(lang, era, page, eras[era], sums.get(page, {}))
                od = os.path.join(OUT, lang, f"main_{era}")
                os.makedirs(od, exist_ok=True)
                with open(os.path.join(od, f"page_{page}.json"), "w", encoding="utf-8") as f:
                    json.dump(ch, f, ensure_ascii=False, indent=1)
                with open(os.path.join(od, f"page_{page}.txt"), "w", encoding="utf-8") as f:
                    f.write(to_txt(ch))
                rows.append([lang, era, page, ch["title"], len(ch["tracks"]), ch["chars"], ch["est_minutes"]])
                t = totals.setdefault(lang, [0, 0, 0, 0.0])
                t[0] += 1; t[1] += len(ch["tracks"]); t[2] += ch["chars"]; t[3] += ch["est_minutes"]

    if not args.era and not args.page:
        with open(os.path.join(OUT, "resumo.csv"), "w", encoding="utf-8", newline="") as f:
            w = csv.writer(f)
            w.writerow(["idioma", "era", "pagina", "titulo", "faixas", "caracteres", "minutos_estimados"])
            w.writerows(rows)

    print(f"{'idioma':6} {'capítulos':>9} {'faixas':>7} {'caracteres':>11} {'horas':>6} {'Chirp3HD US$':>12} {'Gemini US$':>10}")
    for lang, (n, tr, c, mins) in totals.items():
        print(f"{lang:6} {n:9d} {tr:7d} {c:11,d} {mins / 60:6.1f} {c / 1e6 * 30:12.2f} {mins / 60 * 0.90:10.2f}")
    allc = sum(t[2] for t in totals.values()); allm = sum(t[3] for t in totals.values())
    print(f"{'total':6} {sum(t[0] for t in totals.values()):9d} {sum(t[1] for t in totals.values()):7d} {allc:11,d} "
          f"{allm / 60:6.1f} {allc / 1e6 * 30:12.2f} {allm / 60 * 0.90:10.2f}")


if __name__ == "__main__":
    main()
