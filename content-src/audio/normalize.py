"""Normalização de texto para fala (TTS) em pt, en e es.

Regras simples e determinísticas, sem dependências externas. Usado por scripts.py.
Cada função recebe texto já sem HTML (exceto clean_html) e devolve o texto "falado".
"""
import html
import re

# ---------------------------------------------------------------------------
# HTML
# ---------------------------------------------------------------------------
_BRIN = r"((?:[^()]|\([^()]*\))*?)"  # aceita um nível de parênteses dentro do título
_TAGS_BR = re.compile(r"<(a|i|strong|em|b)\b[^>]*>((?:(?!</?(?:a|i|strong|em|b)\b)[^<])*)</(?:a|i|strong|em|b)?>\s*\(BR:\s*" + _BRIN + r"\)", re.S)
_BR_LOOSE = re.compile(r"\(BR:\s*" + _BRIN + r"\)")


def _br_repl(m):
    inner = m.group(3).strip()
    ym = re.match(r"^(.*?)(?:,\s*(\d{4}))?\s*$", inner, re.S)
    title, year = ym.group(1).strip(), ym.group(2)
    return f"{title} ({year})" if year else title


def drop_br_titles(raw):
    """'<a>Le Voyage dans la Lune</a> (BR: Viagem à Lua, 1902)' -> 'Viagem à Lua (1902)'.
    Sem tag em volta do título original, só tira o 'BR:' e mantém os dois títulos."""
    raw = _TAGS_BR.sub(_br_repl, raw)
    return _BR_LOOSE.sub(lambda m: "(" + m.group(1).strip() + ")", raw)


def split_paragraphs(raw):
    """Quebra um HTML em parágrafos (por <br/><br/> ou linhas em branco reais entre frases)."""
    raw = re.sub(r"<script\b.*?</script>", " ", raw, flags=re.S | re.I)
    raw = re.sub(r"(?:<br\s*/?>\s*){2,}", "\n\n<<PARA>>\n\n", raw, flags=re.I)
    raw = re.sub(r"<br\s*/?>", " ", raw, flags=re.I)
    parts = [p for p in raw.split("<<PARA>>")]
    return [p for p in (clean_html(x) for x in parts) if p]


def clean_html(raw):
    raw = drop_br_titles(raw)
    raw = re.sub(r"<script\b.*?</script>", " ", raw, flags=re.S | re.I)
    raw = re.sub(r"<[^>]*>", "", raw)
    raw = html.unescape(raw)
    raw = raw.replace(" ", " ").replace("​", "")
    raw = re.sub(r"[ \t]*\n[ \t\n]*", " ", raw)  # os JSONs têm \n\n no meio da frase
    raw = re.sub(r"\s{2,}", " ", raw)
    raw = re.sub(r"\s+([,.;:!?])", r"\1", raw)
    raw = re.sub(r"[*_#]{2,}", "", raw)
    return raw.strip()


# ---------------------------------------------------------------------------
# Números por extenso
# ---------------------------------------------------------------------------
PT_U = "zero um dois três quatro cinco seis sete oito nove dez onze doze treze catorze quinze dezesseis dezessete dezoito dezenove".split()
PT_T = "_ _ vinte trinta quarenta cinquenta sessenta setenta oitenta noventa".split()
PT_H = "_ cento duzentos trezentos quatrocentos quinhentos seiscentos setecentos oitocentos novecentos".split()

ES_U = ("cero uno dos tres cuatro cinco seis siete ocho nueve diez once doce trece catorce quince dieciséis diecisiete "
        "dieciocho diecinueve veinte veintiuno veintidós veintitrés veinticuatro veinticinco veintiséis veintisiete "
        "veintiocho veintinueve").split()
ES_T = "_ _ veinte treinta cuarenta cincuenta sesenta setenta ochenta noventa".split()
ES_H = "_ ciento doscientos trescientos cuatrocientos quinientos seiscientos setecientos ochocientos novecientos".split()

EN_U = ("zero one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen "
        "seventeen eighteen nineteen").split()
EN_T = "_ _ twenty thirty forty fifty sixty seventy eighty ninety".split()


def _pt_999(n):
    if n < 20:
        return PT_U[n]
    if n < 100:
        t, u = divmod(n, 10)
        return PT_T[t] + ("" if u == 0 else " e " + PT_U[u])
    if n == 100:
        return "cem"
    h, r = divmod(n, 100)
    return PT_H[h] + ("" if r == 0 else " e " + _pt_999(r))


def pt_int(n):
    if n < 1000:
        return _pt_999(n)
    scales = [(10**12, "trilhão", "trilhões"), (10**9, "bilhão", "bilhões"), (10**6, "milhão", "milhões"), (1000, "mil", "mil")]
    parts, rest = [], n
    for val, sing, plur in scales:
        q, rest = divmod(rest, val)
        if q:
            if val == 1000:
                parts.append("mil" if q == 1 else pt_int(q) + " mil")
            else:
                parts.append(pt_int(q) + " " + (sing if q == 1 else plur))
    if rest:
        parts.append(_pt_999(rest))
    # "e" antes do último grupo se ele for < 100 ou centena redonda
    if len(parts) > 1 and (rest and (rest < 100 or rest % 100 == 0)):
        return ", ".join(parts[:-1]).replace(", mil", " mil") + " e " + parts[-1]
    return " ".join(parts) if len(parts) <= 2 else ", ".join(parts)


def _es_999(n, apocope=False):
    if n < 30:
        w = ES_U[n]
        if apocope and n in (1, 21):
            w = "un" if n == 1 else "veintiún"
        return w
    if n < 100:
        t, u = divmod(n, 10)
        if u == 0:
            return ES_T[t]
        return ES_T[t] + " y " + ("un" if (apocope and u == 1) else ES_U[u])
    if n == 100:
        return "cien"
    h, r = divmod(n, 100)
    return ES_H[h] + ("" if r == 0 else " " + _es_999(r, apocope))


def es_int(n, apocope=False):
    if n < 1000:
        return _es_999(n, apocope)
    if n >= 10**12:
        q, r = divmod(n, 10**12)
        head = ("un billón" if q == 1 else es_int(q, True) + " billones")
        return head + ("" if r == 0 else " " + es_int(r, apocope))
    if n >= 10**6:
        q, r = divmod(n, 10**6)
        head = ("un millón" if q == 1 else es_int(q, True) + " millones")
        return head + ("" if r == 0 else " " + es_int(r, apocope))
    q, r = divmod(n, 1000)
    head = "mil" if q == 1 else _es_999(q, True) + " mil"
    return head + ("" if r == 0 else " " + _es_999(r, apocope))


def _en_99(n):
    if n < 20:
        return EN_U[n]
    t, u = divmod(n, 10)
    return EN_T[t] + ("" if u == 0 else "-" + EN_U[u])


def _en_999(n):
    if n < 100:
        return _en_99(n)
    h, r = divmod(n, 100)
    return EN_U[h] + " hundred" + ("" if r == 0 else " " + _en_99(r))


def en_int(n):
    if n < 1000:
        return _en_999(n)
    parts, rest = [], n
    for val, name in [(10**12, "trillion"), (10**9, "billion"), (10**6, "million"), (1000, "thousand")]:
        q, rest = divmod(rest, val)
        if q:
            parts.append(_en_999(q) + " " + name)
    if rest:
        parts.append(_en_999(rest))
    return " ".join(parts)


def en_year(n):
    if 2000 <= n <= 2009:
        return "two thousand" + ("" if n == 2000 else " " + EN_U[n - 2000])
    if 1100 <= n <= 2099 and not (n % 1000 == 0):
        hi, lo = divmod(n, 100)
        if lo == 0:
            return _en_99(hi) + " hundred"
        return _en_99(hi) + " " + ("oh-" + EN_U[lo] if lo < 10 else _en_99(lo))
    return en_int(n)


def en_ordinal_words(w):
    last = re.split(r"([ -])", w)
    word = last[-1]
    irregular = {"one": "first", "two": "second", "three": "third", "five": "fifth", "eight": "eighth",
                 "nine": "ninth", "twelve": "twelfth"}
    if word in irregular:
        word = irregular[word]
    elif word.endswith("y"):
        word = word[:-1] + "ieth"
    else:
        word += "th"
    return "".join(last[:-1]) + word


def en_plural_words(w):
    last = re.split(r"([ -])", w)
    word = last[-1]
    word = word[:-1] + "ies" if word.endswith("y") else word + "s"
    return "".join(last[:-1]) + word


PT_ORD_U = "_ primeiro segundo terceiro quarto quinto sexto sétimo oitavo nono".split()
PT_ORD_T = "_ décimo vigésimo trigésimo quadragésimo quinquagésimo sexagésimo septuagésimo octogésimo nonagésimo".split()
ES_ORD_U = "_ primero segundo tercero cuarto quinto sexto séptimo octavo noveno".split()
ES_ORD_T = "_ décimo vigésimo trigésimo cuadragésimo quincuagésimo sexagésimo septuagésimo octogésimo nonagésimo".split()


def ordinal(n, lang, fem=False):
    if lang == "en":
        return en_ordinal_words(en_int(n))
    if not 1 <= n <= 99:
        return pt_int(n) if lang == "pt" else es_int(n)
    U, T = (PT_ORD_U, PT_ORD_T) if lang == "pt" else (ES_ORD_U, ES_ORD_T)
    t, u = divmod(n, 10)
    words = [w for w in (T[t] if t else "", U[u] if u else "") if w and w != "_"]
    if fem:
        words = [w[:-1] + "a" if w.endswith("o") else w for w in words]
    return " ".join(words)


def cardinal(n, lang, apocope=False):
    return {"pt": pt_int, "en": en_int}.get(lang, lambda x: es_int(x, apocope))(n)


def year_words(n, lang):
    return en_year(n) if lang == "en" else cardinal(n, lang)


# ---------------------------------------------------------------------------
# Números no texto
# ---------------------------------------------------------------------------
DEC_SEP = {"pt": ",", "es": ",", "en": "."}
THO_SEP = {"pt": ".", "es": ".", "en": ","}
DEC_WORD = {"pt": "vírgula", "es": "coma", "en": "point"}

NUM_RE = r"\d{1,3}(?:[.,]\d{3})+(?:[.,]\d+)?|\d+(?:[.,]\d+)?"


def parse_number(s, lang):
    """Devolve (inteiro, decimais_str|None)."""
    seps = re.findall(r"[.,]", s)
    if not seps:
        return int(s), None
    groups = re.split(r"[.,]", s)
    # vários separadores ou o separador de milhar da língua com grupos de 3 dígitos => inteiro
    if len(seps) >= 2 and all(len(g) == 3 for g in groups[1:]) and len(set(seps)) == 1:
        return int("".join(groups)), None
    if len(seps) >= 2:  # 1.234,5 / 1,234.5
        dec = seps[-1]
        intpart, decpart = s.rsplit(dec, 1)
        return int(re.sub(r"[.,]", "", intpart)), decpart
    sep = seps[0]
    # um único separador seguido de 3 dígitos = milhar (vale também para "20,000 Leagues" em texto pt/es)
    if len(groups[1]) == 3 and groups[0] != "0":
        return int("".join(groups)), None
    return int(groups[0]), groups[1]


def number_words(s, lang, apocope=False):
    i, dec = parse_number(s, lang)
    w = cardinal(i, lang, apocope)
    if dec:
        # "4,5" -> quatro vírgula cinco; "1,25" -> um vírgula vinte e cinco; zeros à esquerda dígito a dígito
        lead = len(dec) - len(dec.lstrip("0"))
        tail = dec.lstrip("0")
        dw = " ".join([cardinal(0, lang)] * lead + ([cardinal(int(tail), lang, apocope)] if tail else []))
        w += f" {DEC_WORD[lang]} {dw}"
    return w, (dec is not None and dec.strip("0") != "") or i != 1


SCALES = {
    "pt": {"mil": ("mil", "mil"), "milhão": ("milhão", "milhões"), "milhões": ("milhão", "milhões"),
           "bilhão": ("bilhão", "bilhões"), "bilhões": ("bilhão", "bilhões"), "trilhão": ("trilhão", "trilhões"),
           "trilhões": ("trilhão", "trilhões")},
    "en": {"thousand": ("thousand", "thousand"), "million": ("million", "million"), "billion": ("billion", "billion"),
           "trillion": ("trillion", "trillion")},
    "es": {"mil millones": ("mil millones", "mil millones"), "mil": ("mil", "mil"), "millón": ("millón", "millones"),
           "millones": ("millón", "millones"), "billón": ("billón", "billones"), "billones": ("billón", "billones")},
}
CURRENCY = {
    "pt": {"US$": ("dólar", "dólares"), "U$": ("dólar", "dólares"), "$": ("dólar", "dólares"), "R$": ("real", "reais"), "€": ("euro", "euros"),
           "£": ("libra", "libras"), "¥": ("iene", "ienes")},
    "en": {"US$": ("dollar", "dollars"), "U$": ("dollar", "dollars"), "$": ("dollar", "dollars"), "R$": ("real", "reais"), "€": ("euro", "euros"),
           "£": ("pound", "pounds"), "¥": ("yen", "yen")},
    "es": {"US$": ("dólar", "dólares"), "U$": ("dólar", "dólares"), "$": ("dólar", "dólares"), "R$": ("real", "reales"), "€": ("euro", "euros"),
           "£": ("libra", "libras"), "¥": ("yen", "yenes")},
}


def _money(lang):
    scales = "|".join(sorted((re.escape(k) for k in SCALES[lang]), key=len, reverse=True))
    return re.compile(r"(US\$|U\$|R\$|\$|€|£|¥)\s?(" + NUM_RE + r")(?:\s(" + scales + r"))?\b")


_MONEY = {l: _money(l) for l in SCALES}


def money_repl(m, lang):
    cur, num, scale = m.group(1), m.group(2), m.group(3)
    words, plural = number_words(num, lang, apocope=(lang == "es"))
    sing, plur = CURRENCY[lang][cur]
    if scale:
        s_sing, s_plur = SCALES[lang][scale]
        sc = s_plur if plural else s_sing
        if lang == "en":
            return f"{words} {sc} {plur}"
        if lang == "es" and scale == "mil":
            return f"{words} mil {plur}"
        if lang == "pt" and scale == "mil":
            return f"{words} mil {plur}"
        return f"{words} {sc} de {plur}"
    i, _ = parse_number(num, lang)
    of = ""
    if lang in ("pt", "es") and i >= 10**6 and i % 10**6 == 0:
        of = "de "
    return f"{words} {of}{plur if plural else sing}"


ROMAN_VALS = {"I": 1, "V": 5, "X": 10, "L": 50}


def roman_to_int(s):
    total, prev = 0, 0
    for ch in reversed(s):
        v = ROMAN_VALS[ch]
        total = total - v if v < prev else total + v
        prev = max(prev, v)
    return total


ROMAN_RE = r"(?:X{0,3})(?:IX|IV|V?I{0,3})"
REGNAL = {
    "pt": r"Henrique|Luís|Luiz|Ricardo|Carlos|Jorge|Eduardo|Elizabeth|Elizabete|Isabel|Pedro|João|Papa|Pio|Bento|Napoleão|Guilherme|Frederico|Felipe|Filipe|Leão",
    "es": r"Enrique|Luis|Ricardo|Carlos|Jorge|Eduardo|Isabel|Pedro|Juan|Papa|Pío|Benedicto|Napoleón|Guillermo|Federico|Felipe|León|Alfonso",
    "en": r"Henry|Louis|Richard|Charles|George|Edward|Elizabeth|Peter|John|Pope|Pius|Benedict|Napoleon|William|Frederick|Philip|Leo",
}
CENTURY = {"pt": r"século|séculos", "es": r"siglo|siglos", "en": r"[Cc]entury"}


def _roman(text, lang):
    # século XX -> século vinte
    text = re.sub(r"\b(" + CENTURY[lang] + r")\s+(" + ROMAN_RE + r")\b",
                  lambda m: m.group(1) + " " + cardinal(roman_to_int(m.group(2)), lang) if m.group(2) else m.group(0), text)
    # II Guerra Mundial -> Segunda Guerra Mundial ; World War II -> World War Two
    if lang == "en":
        text = re.sub(r"\bWorld War (II|I)\b", lambda m: "World War " + ("Two" if m.group(1) == "II" else "One"), text)
    else:
        first = {"pt": ("Primeira", "Segunda"), "es": ("Primera", "Segunda")}[lang]
        text = re.sub(r"\b(I|II)\s+Guerra Mundial\b", lambda m: first[len(m.group(1)) - 1] + " Guerra Mundial", text)
    # Henrique V -> Henrique quinto (reis/papas)
    text = re.sub(r"\b(" + REGNAL[lang] + r")\s+(" + ROMAN_RE + r")\b",
                  lambda m: m.group(1) + (" the " if lang == "en" else " ") + ordinal(roman_to_int(m.group(2)), lang).title()
                  if m.group(2) else m.group(0), text)
    # Parte II, Rocky III, Will Turner III -> cardinal (2 a 39, só com 2+ letras para não pegar o pronome "I")
    def card(m):
        r = m.group(2)
        if len(r) < 2 or not re.fullmatch(ROMAN_RE, r):
            return m.group(0)
        return m.group(1) + cardinal(roman_to_int(r), lang).capitalize()
    text = re.sub(r"(\b[A-ZÀ-Ý][\wÀ-ÿ'’]*:?\s)([IVX]{2,6})\b(?![-'’])", card, text)
    return text


ACRONYM_SPELL = {
    "pt": "CGI HBO MGM RKO FBI CIA BBC DVD VFX ADR CG DC HD MIT LAPD AMC CBS MCU BFI MPAA NBC ABC AFI MTV TMDB "
          "NAACP IA UFC ONG PC IBM NYU USC WGA DGA MPA EA LGBT LGBTQ LGBTQIA RRR ILM CNN VHS LSD FCC UCI WB AP NPR "
          "MPC ATG AMPTP SBT INR DCEU OMS CD",
    "es": "CGI HBO MGM RKO FBI CIA BBC DVD VFX ADR CG DC HD MIT LAPD AMC CBS MCU BFI MPAA NBC ABC AFI MTV TMDB "
          "NAACP IA ONG PC IBM NYU USC WGA DGA MPA EA LGBT LGBTQ LGBTQIA RRR ILM CNN VHS LSD FCC UCI WB AP NPR "
          "MPC ATG AMPTP INR DCEU OMS CD ADN",
}
LETTERS = {
    "pt": dict(zip("ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                   "á bê cê dê é éfe gê agá i jota cá éle ême êne ó pê quê érre ésse tê u vê dáblio xis ípsilon zê".split())),
    "es": dict(zip("ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                   "a be ce de e efe ge hache i jota ka ele eme ene o pe cu erre ese te u uve uve_doble equis ye zeta".split())),
}
ACRONYM_WORDS = {
    "pt": {"SAG-AFTRA": "Sag Áftra", "20ht": "20th", "EUA": "Estados Unidos", "URSS": "União Soviética", "TV": "tevê", "nº": "número", "n.º": "número",
           "etc.": "etcétera", "vs.": "versus", "&": "e", "LGBTQIA+": "LGBTQIA mais", "Sr.": "senhor", "Sra.": "senhora",
           "Dr.": "doutor", "Dra.": "doutora", "a.C.": "antes de Cristo", "d.C.": "depois de Cristo"},
    "es": {"SAG-AFTRA": "Sag Áftra", "20ht": "20th", "EE. UU.": "Estados Unidos", "EE.UU.": "Estados Unidos", "EEUU": "Estados Unidos", "URSS": "Unión Soviética",
           "TV": "tele", "nº": "número", "n.º": "número", "etc.": "etcétera", "vs.": "versus", "&": "y",
           "LGBTQIA+": "LGBTQIA más", "Sr.": "señor", "Sra.": "señora", "Dr.": "doctor", "a. C.": "antes de Cristo",
           "a.C.": "antes de Cristo"},
    "en": {"20ht": "20th", "vs.": "versus", "&": "and", "LGBTQIA+": "LGBTQIA plus", "U.S.": "U.S.", "no.": "number"},
}


def _acronyms(text, lang):
    for k, v in sorted(ACRONYM_WORDS[lang].items(), key=lambda kv: -len(kv[0])):
        if re.match(r"\w", k[0]):
            text = re.sub(r"(?<![\w.])" + re.escape(k) + (r"(?!\w)" if not k[-1].isalnum() else r"\b"), v, text)
        else:
            text = text.replace(" " + k + " ", " " + v + " ")
    if lang in ACRONYM_SPELL:
        for a in ACRONYM_SPELL[lang].split():
            a = a.rstrip("_")
            spelled = " ".join(LETTERS[lang][c].replace("_", " ") for c in a)
            text = re.sub(r"(?<![\w-])" + a + r"(?![\w])", spelled, text)
    return text


def normalize_numbers(text, lang):
    # moedas
    text = _MONEY[lang].sub(lambda m: money_repl(m, lang), text)
    # 007 -> zero zero sete / double-oh-seven
    if lang == "en":
        text = re.sub(r"\b007\b", "double-oh-seven", text)
    text = re.sub(r"(?<![\d.,])\b(0\d{1,3})\b(?![.,]\d)", lambda m: " ".join(cardinal(int(d), lang) for d in m.group(1)), text)
    # datas em inglês: "December 25, 1895" / "on March 4" -> ordinal
    if lang == "en":
        months = "January|February|March|April|May|June|July|August|September|October|November|December"
        text = re.sub(r"\b(" + months + r")\s(\d{1,2})\b(?!\d)", lambda m: f"{m.group(1)} {en_ordinal_words(en_int(int(m.group(2))))}", text)
        text = re.sub(r"\b(\d{1,2})\s(" + months + r")\b", lambda m: f"the {en_ordinal_words(en_int(int(m.group(1))))} of {m.group(2)}", text)
    # "1,5 milhão" -> "um vírgula cinco milhões" (decimal pede plural na fala)
    sing_plur = {"pt": [("milhão", "milhões"), ("bilhão", "bilhões"), ("trilhão", "trilhões")],
                 "es": [("millón", "millones"), ("billón", "billones")], "en": []}[lang]
    for sg, pl in sing_plur:
        text = re.sub(r"(\d+" + re.escape(DEC_SEP[lang]) + r"\d+\s)" + sg + r"\b", r"\g<1>" + pl, text)
    # (n-1930) / (b. 1930) -> (nasceu em mil novecentos e trinta)
    born = {"pt": "nasceu em", "es": "nació en", "en": "born in"}[lang]
    text = re.sub(r"\((?:n|b|nac|nasc)\s?[-.]?\s?(\d{4})\)", lambda m: f"({born} {m.group(1)})", text)
    # 2h15 -> duas horas e quinze ; 70mm ; 2D/3D
    hrs = {"pt": ("hora", "horas", "e"), "es": ("hora", "horas", "y"), "en": ("hour", "hours", "")}[lang]
    text = re.sub(r"\b(\d{1,2})h(\d{2})?\b(?!\w)", lambda m: f"{cardinal(int(m.group(1)), lang) if lang == 'en' else to_feminine(cardinal(int(m.group(1)), lang), lang)} "
                  f"{hrs[0] if m.group(1) == '1' else hrs[1]}" + (f" {hrs[2]} {m.group(2)}".replace("  ", " ") if m.group(2) else ""), text)
    mm = {"pt": "milímetros", "es": "milímetros", "en": "millimeter"}[lang]
    text = re.sub(r"\b(\d{1,3})\s?mm\b", lambda m: f"{m.group(1)} {mm}", text)
    if lang != "en":
        dee = {"pt": "dê", "es": "de"}[lang]
        text = re.sub(r"\b([234])D\b", lambda m: f"{cardinal(int(m.group(1)), lang)} {dee}", text)
    # porcentagem
    pct = {"pt": "por cento", "es": "por ciento", "en": "percent"}[lang]
    text = re.sub(r"(" + NUM_RE + r")\s?%", lambda m: number_words(m.group(1), lang)[0] + " " + pct, text)
    # ordinais em inglês (também aparecem em nomes como "20th Century Fox" nos textos pt/es)
    text = re.sub(r"\b(\d+)(st|nd|rd|th)\b(?=(\s[A-Z])?)", lambda m: en_ordinal_words(en_int(int(m.group(1)))).capitalize()
                  if (lang != "en" or m.group(3)) else en_ordinal_words(en_int(int(m.group(1)))), text)
    # ordinais pt/es: 1º, 2ª, 3.º
    if lang != "en":
        text = re.sub(r"\b(\d{1,2})\.?([ºª°])", lambda m: ordinal(int(m.group(1)), lang, fem=m.group(2) == "ª"), text)
    # décadas: 1920s -> nineteen twenties ; '70s -> seventies ; anos 20 fica por extenso normal
    if lang == "en":
        text = re.sub(r"\b(1[1-9]\d0|20[0-9]0)['’]?s\b", lambda m: en_plural_words(en_year(int(m.group(1)))), text)
        text = re.sub(r"['’](\d)0s\b", lambda m: en_plural_words(_en_99(int(m.group(1)) * 10)), text)
        text = re.sub(r"\b([2-9])0s\b", lambda m: en_plural_words(_en_99(int(m.group(1)) * 10)), text)
    else:
        text = re.sub(r"\b(\d{4})s\b", r"\1", text)
    # intervalos de anos: 1861-1938, 1939–45
    to = {"pt": "a", "es": "a", "en": "to"}[lang]

    def rng(m):
        a, b = m.group(1), m.group(2)
        if len(b) == 2:
            b = a[:2] + b
        if int(b) < int(a):
            return m.group(0)
        return f"{year_words(int(a), lang)} {to} {year_words(int(b), lang)}"
    text = re.sub(r"\b(1[0-9]\d\d|20\d\d)\s?[-–—/]\s?(1[0-9]\d\d|20\d\d|\d\d)\b(?!\d)", rng, text)
    text = re.sub(r"(?<=\()(1[0-9]\d\d|20\d\d)\s(1[0-9]\d\d|20\d\d)(?=\))", rng, text)  # "(1913 1914)"
    # ano entre parênteses depois de título: "Viagem à Lua (1902)" -> "Viagem à Lua, de 1902,"
    of_year = {"pt": "de", "es": "de", "en": "from"}[lang]
    text = re.sub(r"\s?\((1[0-9]\d\d|20\d\d)\)", lambda m: f", {of_year} {year_words(int(m.group(1)), lang)},", text)
    # anos soltos (só muda algo no inglês: nineteen oh-two)
    text = re.sub(r"(?<![\d.,])\b(1[1-9]\d\d|20\d\d)\b(?![.,]\d)", lambda m: year_words(int(m.group(1)), lang), text)
    # demais números (com escala depois: "50 milhões" já fica certo); concorda com substantivos femininos comuns
    def plain(m):
        w = number_words(m.group(1), lang, apocope=(lang == "es"))[0]
        nxt = (m.group(2) or "").lower()
        if nxt and nxt in FEMININE.get(lang, ()):
            w = to_feminine(w, lang)
        return w + (m.group(0)[len(m.group(1)):])
    text = re.sub(r"(?<![\w])(" + NUM_RE + r")(?![\w])(?=\s(?:de\s)?([\wÀ-ÿ]+))?", plain, text)
    return text


FEMININE = {
    "pt": set("pessoas pessoa vezes vez semanas horas salas cópias indicações indicação estatuetas obras línguas cenas "
              "páginas edições temporadas mulheres atrizes diretoras sessões telas cidades décadas categorias "
              "produções séries histórias versões partes noites horas".split()),
    "es": set("personas persona veces semanas horas salas copias nominaciones estatuillas obras lenguas escenas "
              "páginas ediciones temporadas mujeres actrices directoras sesiones pantallas ciudades décadas categorías "
              "producciones series historias versiones partes noches".split()),
}


def to_feminine(w, lang):
    if lang == "pt":
        w = re.sub(r"\bum\b", "uma", w)
        w = re.sub(r"\bdois\b", "duas", w)
        return re.sub(r"entos\b", "entas", w)
    w = re.sub(r"\b(un|uno)\b", "una", w)
    w = re.sub(r"veintiún\b|veintiuno\b", "veintiuna", w)
    return re.sub(r"ientos\b", "ientas", w)


def tidy(text):
    text = re.sub(r",\s*,", ",", text)
    text = re.sub(r",\s*([.;:!?)])", r"\1", text)
    text = re.sub(r"\(\s*,\s*", "(", text)
    text = re.sub(r"\s{2,}", " ", text)
    text = re.sub(r"\s+([,.;:!?])", r"\1", text)
    text = re.sub(r"\(\s*\)", "", text)
    return text.strip()


# ---------------------------------------------------------------------------
# Pronúncia: nomes estrangeiros que o TTS costuma errar.
# Marcação no roteiro: [[pron: Original|grafia fonética]]. O generate.py troca pela grafia
# fonética (ou mantém o original, se preferir) antes de mandar para o TTS.
# Lista básica: confira ouvindo a amostra e ajuste.
# ---------------------------------------------------------------------------
PRON = {
    "pt": {
        r"M[ée]li[èe]s": "Meliés", r"Lumi[èe]re": "Lumiér", r"Truffaut": "Trufô", r"Godard": "Godár",
        r"Renoir": "Renuár", r"Eisenstein": "Áizenstáin", r"Bu[ñn]uel": "Bunhuél", r"Hitchcock": "Rítchcók",
        r"Griffith": "Grífit", r"Keaton": "Kíton", r"Welles": "Uéls", r"Scorsese": "Scorsézi",
        r"Spielberg": "Spílberg", r"Kubrick": "Kúbrik", r"Tarkovsky": "Tarkóvski", r"Fassbinder": "Fásbinder",
        r"Herzog": "Hértsog", r"Wenders": "Vénders", r"Murnau": "Múrnau", r"Bergman": "Bérigman",
        r"Kie[sś]lowski": "Kieslófski", r"Wong Kar[- ]wai": "Uóng Kar Uái", r"Kurosawa": "Kurossaua",
        r"Miyazaki": "Miazáki", r"Ozu": "Ózu", r"Almod[óo]var": "Almodóvar", r"Pathé": "Patê",
        r"Gaumont": "Gomôn", r"Cahiers du Cinéma": "Caiê du Cinemá", r"Nouvelle Vague": "Nuvél Vág",
    },
    "en": {
        r"M[ée]li[èe]s": "May-lee-ess", r"Lumi[èe]re": "Loo-mee-air", r"Truffaut": "Troo-foe", r"Godard": "Go-dar",
        r"Renoir": "Ren-wahr", r"Eisenstein": "Eye-zen-stine", r"Bu[ñn]uel": "Boon-yoo-ell", r"Kie[sś]lowski": "Kyesh-loff-skee",
        r"Wong Kar[- ]wai": "Wong Kar-why", r"Herzog": "Hair-tsog", r"Pathé": "Pa-tay", r"Gaumont": "Go-mon",
        r"Cahiers du Cinéma": "Ka-yay doo See-nay-mah", r"Nouvelle Vague": "Noo-vel Vahg", r"Almod[óo]var": "Al-mo-DOH-var",
        r"Iñárritu": "Ee-NYAR-ee-too", r"Cuarón": "Kwa-RONE",
    },
    "es": {
        r"M[ée]li[èe]s": "Meliés", r"Lumi[èe]re": "Lumier", r"Truffaut": "Trufó", r"Godard": "Godar",
        r"Renoir": "Renuar", r"Eisenstein": "Áisenstain", r"Hitchcock": "Jíchcoc", r"Griffith": "Grífit",
        r"Keaton": "Kíton", r"Welles": "Uels", r"Spielberg": "Spílberg", r"Herzog": "Hértsog",
        r"Kie[sś]lowski": "Kieslovski", r"Wong Kar[- ]wai": "Uong Kar Uai", r"Pathé": "Paté", r"Gaumont": "Gomón",
        r"Cahiers du Cinéma": "Caié du Ciné", r"Nouvelle Vague": "Nuvel Vag",
    },
}


def mark_pron(text, lang):
    for pat, resp in PRON[lang].items():
        text = re.sub(r"(?<![\w\[|])(" + pat + r")(?![\w\]|])", lambda m: f"[[pron: {m.group(1)}|{resp}]]", text)
    return text


PRON_MARK = re.compile(r"\[\[pron:\s*([^|\]]+)\|([^\]]+)\]\]")


def render_pron(text, mode="original"):
    """mode='original' mantém o nome escrito; 'respell' usa a grafia fonética."""
    return PRON_MARK.sub(lambda m: m.group(1) if mode == "original" else m.group(2), text)


def speak(text, lang):
    """Texto limpo (sem HTML) -> texto falado com marcas [[pron]]."""
    t = re.sub(r"\s*[–—]\s*([,.;:])", r"\1", text)
    t = re.sub(r"\s*[–—]\s*", ", ", t)
    t = t.replace("…", "...")
    t = re.sub(r"[“”«»„]", '"', t)
    t = _acronyms(t, lang)
    t = _roman(t, lang)
    t = normalize_numbers(t, lang)
    t = tidy(t)
    t = mark_pron(t, lang)
    return t
