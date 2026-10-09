#!/usr/bin/env python3
"""Gera o áudio (Opus/ogg ~48 kbps, uma faixa por arquivo) a partir dos roteiros de roteiros/.

Provedores:
  chirp   Google Cloud Text-to-Speech, vozes Chirp 3 HD (US$ 30 / 1 milhão de caracteres; 1 milhão grátis/mês)
          Autenticação: GOOGLE_TTS_API_KEY  ou  GOOGLE_ACCESS_TOKEN (gcloud auth print-access-token)
                        + GOOGLE_CLOUD_PROJECT (cobrança do projeto, só com o token)
  gemini  Gemini API TTS (cobrado por tokens de áudio; ~US$ 0,90/hora, ver PRECO_GEMINI_HORA)
          Autenticação: GEMINI_API_KEY

Dependências: Python 3.9+ (só biblioteca padrão) e o programa ffmpeg com libopus no PATH.

Exemplos:
  python3 generate.py --provider chirp --dry-run                       # custo estimado, nada é chamado
  python3 generate.py --provider chirp --sample pt 1 1 --voices Charon,Iapetus,Kore
  python3 generate.py --provider chirp --langs pt --era 1 --page 1     # um capítulo
  python3 generate.py --provider chirp --langs pt                      # tudo em pt (só o que mudou)

Saída: out/<idioma>/main_<era>/page_<n>/<faixa>.ogg  +  manifest.json por capítulo.
Cache: cache/<provedor>/<hash>.pcm por segmento (hash do texto + voz + config): mudou um parágrafo,
       só ele é sintetizado de novo; a faixa é remontada e o manifest atualizado.
"""
import argparse
import base64
import hashlib
import io
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import time
import urllib.error
import urllib.request
import wave

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from normalize import render_pron  # noqa: E402

ROTEIROS = os.path.join(HERE, "roteiros")
OUT = os.path.join(HERE, "out")
CACHE = os.path.join(HERE, "cache")
VOZES = os.path.join(HERE, "vozes.json")

PRECO_CHIRP_MILHAO = 30.0        # US$ por 1 milhão de caracteres (Chirp 3 HD)
FREE_CHIRP_MES = 1_000_000       # caracteres grátis por mês (Chirp 3 HD)
PRECO_GEMINI_HORA = 0.90         # US$ por hora de áudio (estimativa; doc de out/2026: 3.8 Flash TTS = US$ 0,81/h até 31/12/2026)
CHARS_PER_MIN = {"pt": 900, "en": 1000, "es": 950}
BITRATE = "48k"
MAX_BYTES_REQ = 4000             # Cloud TTS aceita até 5000 bytes por requisição; folga para acentos

CHIRP_URL = "https://texttospeech.googleapis.com/v1/text:synthesize"
GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/interactions"


# ---------------------------------------------------------------------------
# utilidades
# ---------------------------------------------------------------------------
def load_json(p):
    with open(p, encoding="utf-8") as f:
        return json.load(f)


def sha(*parts):
    h = hashlib.sha256()
    for p in parts:
        h.update(json.dumps(p, ensure_ascii=False, sort_keys=True).encode("utf-8"))
        h.update(b"\x00")
    return h.hexdigest()[:20]


def chapters(langs, era=None, page=None):
    for lang in langs:
        base = os.path.join(ROTEIROS, lang)
        if not os.path.isdir(base):
            continue
        for d in sorted(os.listdir(base), key=lambda x: int(x.split("_")[1])):
            e = int(d.split("_")[1])
            if era and e != era:
                continue
            files = [f for f in os.listdir(os.path.join(base, d)) if f.endswith(".json")]
            for f in sorted(files, key=lambda x: int(re.search(r"\d+", x).group())):
                p = int(re.search(r"\d+", f).group())
                if page and p != page:
                    continue
                yield load_json(os.path.join(base, d, f))


def split_for_request(text, max_bytes=MAX_BYTES_REQ):
    """Quebra em frases para respeitar o limite de bytes por requisição."""
    if len(text.encode("utf-8")) <= max_bytes:
        return [text]
    out, buf = [], ""
    for s in re.split(r"(?<=[.!?;])\s+", text):
        cand = (buf + " " + s).strip()
        if buf and len(cand.encode("utf-8")) > max_bytes:
            out.append(buf)
            buf = s
        else:
            buf = cand
    if buf:
        out.append(buf)
    return out


def wav_to_pcm(data, want_rate):
    """Aceita WAV (com cabeçalho RIFF) ou PCM cru 16-bit mono; devolve PCM no sample rate pedido."""
    if data[:4] == b"RIFF":
        with wave.open(io.BytesIO(data)) as w:
            rate, ch, pcm = w.getframerate(), w.getnchannels(), w.readframes(w.getnframes())
        if ch != 1 or rate != want_rate:
            raise RuntimeError(f"áudio inesperado: {ch} canais, {rate} Hz (esperado mono {want_rate} Hz)")
        return pcm
    return data


def silence(ms, rate):
    return b"\x00\x00" * int(rate * ms / 1000)


def http_post(url, body, headers, tries=5):
    data = json.dumps(body).encode("utf-8")
    for i in range(tries):
        req = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json", **headers})
        try:
            with urllib.request.urlopen(req, timeout=180) as r:
                return json.loads(r.read())
        except urllib.error.HTTPError as e:
            msg = e.read().decode("utf-8", "replace")[:500]
            if e.code in (429, 500, 502, 503, 504) and i < tries - 1:
                time.sleep(2 ** i * 3)
                continue
            raise RuntimeError(f"HTTP {e.code}: {msg}") from None
        except urllib.error.URLError:
            if i < tries - 1:
                time.sleep(2 ** i * 3)
                continue
            raise


# ---------------------------------------------------------------------------
# provedores: cada um recebe (texto, speaker, lang, voz_override) e devolve PCM 16-bit mono
# ---------------------------------------------------------------------------
class Chirp:
    name = "chirp"

    def __init__(self, cfg):
        self.cfg = cfg["chirp"]
        self.rate = self.cfg.get("sample_rate", 24000)

    def voice(self, lang, speaker, override=None):
        c = self.cfg[lang]
        v = override or c["narrador" if speaker == "narrador" else "citacao"]
        if "-Chirp3-HD-" not in v:
            v = f"{c['locale']}-Chirp3-HD-{v}"
        return v

    def key(self, text, lang, speaker, override=None):
        return {"p": "chirp", "v": self.voice(lang, speaker, override), "r": self.cfg.get("speaking_rate", 1.0),
                "sr": self.rate, "t": text}

    def synth(self, text, lang, speaker, override=None):
        voice = self.voice(lang, speaker, override)
        headers, url = {}, CHIRP_URL
        if os.environ.get("GOOGLE_TTS_API_KEY"):
            headers["x-goog-api-key"] = os.environ["GOOGLE_TTS_API_KEY"]
        elif os.environ.get("GOOGLE_ACCESS_TOKEN"):
            headers["Authorization"] = "Bearer " + os.environ["GOOGLE_ACCESS_TOKEN"]
            if os.environ.get("GOOGLE_CLOUD_PROJECT"):
                headers["x-goog-user-project"] = os.environ["GOOGLE_CLOUD_PROJECT"]
        else:
            sys.exit("Defina GOOGLE_TTS_API_KEY (ou GOOGLE_ACCESS_TOKEN + GOOGLE_CLOUD_PROJECT).")
        pcm = b""
        for chunk in split_for_request(text):
            body = {"input": {"text": chunk},
                    "voice": {"languageCode": voice.split("-Chirp3")[0], "name": voice},
                    "audioConfig": {"audioEncoding": "LINEAR16", "sampleRateHertz": self.rate,
                                    "speakingRate": self.cfg.get("speaking_rate", 1.0)}}
            r = http_post(url, body, headers)
            pcm += wav_to_pcm(base64.b64decode(r["audioContent"]), self.rate)
        return pcm


class Gemini:
    name = "gemini"
    rate = 24000  # a Gemini API devolve WAV 24 kHz mono 16-bit

    def __init__(self, cfg):
        self.cfg = cfg["gemini"]

    def voice(self, lang, speaker, override=None):
        return override or self.cfg[lang]["narrador" if speaker == "narrador" else "citacao"]

    def style(self, lang, speaker):
        return self.cfg[lang]["estilo_narrador" if speaker == "narrador" else "estilo_citacao"]

    def key(self, text, lang, speaker, override=None):
        return {"p": "gemini", "m": self.cfg["model"], "v": self.voice(lang, speaker, override),
                "s": self.style(lang, speaker), "t": text}

    def synth(self, text, lang, speaker, override=None):
        key = os.environ.get("GEMINI_API_KEY") or sys.exit("Defina GEMINI_API_KEY.")
        pcm = b""
        for chunk in split_for_request(text, 3000):
            body = {
                "model": self.cfg["model"],
                "input": [{"type": "user_input", "content": [{
                    "type": "text", "text": chunk,
                    "annotations": [{"type": "speech_metadata", "style": self.style(lang, speaker)}]}]}],
                "response_format": {"type": "audio"},
                "generation_config": {"speech_config": [{"voice": self.voice(lang, speaker, override)}]},
            }
            r = http_post(GEMINI_URL, body, {"x-goog-api-key": key})
            pcm += wav_to_pcm(base64.b64decode(self._audio(r)), self.rate)
        return pcm

    @staticmethod
    def _audio(resp):
        """steps[].content[] com type == audio (doc de out/2026). Busca recursiva para tolerar mudanças."""
        found = []

        def walk(x):
            if isinstance(x, dict):
                if x.get("type") == "audio" and isinstance(x.get("data"), str):
                    found.append(x["data"])
                for v in x.values():
                    walk(v)
            elif isinstance(x, list):
                for v in x:
                    walk(v)
        walk(resp)
        if not found:
            raise RuntimeError("resposta sem áudio: " + json.dumps(resp)[:400])
        return found[-1]


PROVIDERS = {"chirp": Chirp, "gemini": Gemini}


# ---------------------------------------------------------------------------
# montagem das faixas
# ---------------------------------------------------------------------------
def seg_text(seg, cfg):
    return render_pron(seg["text"], cfg.get("pron", "original"))


def synth_cached(prov, text, lang, speaker, override=None):
    k = sha(prov.key(text, lang, speaker, override))
    d = os.path.join(CACHE, prov.name)
    os.makedirs(d, exist_ok=True)
    path = os.path.join(d, k + ".pcm")
    if os.path.exists(path):
        with open(path, "rb") as f:
            return f.read()
    pcm = prov.synth(text, lang, speaker, override)
    tmp = path + ".tmp"
    with open(tmp, "wb") as f:
        f.write(pcm)
    os.replace(tmp, path)
    return pcm


def build_track_pcm(prov, track, lang, cfg, narrator_override=None):
    p = cfg.get("pausas_ms", {})
    out = [silence(p.get("inicio_faixa", 250), prov.rate)]
    marks = []
    prev = None
    for seg in track["segments"]:
        if prev is not None:
            gap = p.get("citacao", 700) if "citacao" in (prev, seg["speaker"]) else p.get("paragrafo", 450)
            out.append(silence(gap, prov.rate))
        ov = narrator_override if seg["speaker"] == "narrador" else None
        # source_index: item de content_list do capítulo (-1 = abertura); o app usa para destacar o trecho
        marks.append({"seg": seg["id"], "t": round(sum(len(x) for x in out) / 2 / prov.rate, 2),
                      "source_index": seg.get("source_index", -1)})
        out.append(synth_cached(prov, seg_text(seg, cfg), lang, seg["speaker"], ov))
        prev = seg["speaker"]
    out.append(silence(p.get("fim_faixa", 600), prov.rate))
    return b"".join(out), marks


def encode_opus(pcm, rate, dest, title=None):
    if not shutil.which("ffmpeg"):
        sys.exit("ffmpeg não encontrado no PATH (precisa de libopus).")
    os.makedirs(os.path.dirname(dest), exist_ok=True)
    with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp:
        with wave.open(tmp, "wb") as w:
            w.setnchannels(1)
            w.setsampwidth(2)
            w.setframerate(rate)
            w.writeframes(pcm)
        wav = tmp.name
    cmd = ["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-c:a", "libopus", "-b:a", BITRATE,
           "-vbr", "on", "-application", "voip", "-ac", "1"]
    if title:
        cmd += ["-metadata", f"title={title}"]
    subprocess.run(cmd + [dest], check=True)
    os.unlink(wav)
    return len(pcm) / 2 / rate


def track_hash(prov, track, lang, cfg):
    return sha([prov.key(seg_text(s, cfg), lang, s["speaker"]) for s in track["segments"]],
               cfg.get("pausas_ms"), BITRATE)


def generate_chapter(prov, ch, cfg, force=False):
    lang, era, page = ch["lang"], ch["era"], ch["page"]
    cdir = os.path.join(OUT, lang, f"main_{era}", f"page_{page}")
    mpath = os.path.join(cdir, "manifest.json")
    old = {t["id"]: t for t in load_json(mpath)["tracks"]} if os.path.exists(mpath) else {}
    tracks = []
    for t in ch["tracks"]:
        h = track_hash(prov, t, lang, cfg)
        # o hash no nome: áudio novo = endereço novo (o cache do servidor e o offline do app nunca ficam velhos)
        fname = f"{t['id']}-{h[:10]}.ogg"
        dest = os.path.join(cdir, fname)
        if not force and old.get(t["id"], {}).get("hash") == h and os.path.exists(os.path.join(cdir, old[t["id"]].get("file", ""))):
            kept = dict(old[t["id"]])
            # o source_index não entra no hash (não muda o áudio): atualiza as marcas com o roteiro atual
            src = {sg["id"]: sg.get("source_index", -1) for sg in t["segments"]}
            kept["marks"] = [{**m, "source_index": src.get(m["seg"], m.get("source_index", -1))}
                             for m in kept.get("marks", [])]
            kept["title"], kept["kind"] = t["title"], t.get("kind")
            tracks.append(kept)
            continue
        print(f"  {lang} era {era} cap {page} faixa {t['id']} {t['title'][:50]!r} ({t['chars']} car.)")
        pcm, marks = build_track_pcm(prov, t, lang, cfg)
        dur = encode_opus(pcm, prov.rate, dest, t["title"])
        tracks.append({"id": t["id"], "title": t["title"], "kind": t.get("kind"), "file": fname,
                       "duration_s": round(dur, 2), "bytes": os.path.getsize(dest), "chars": t["chars"], "hash": h,
                       "marks": marks})
    # remove arquivos de faixas que deixaram de existir
    keep = {t["file"] for t in tracks}
    if os.path.isdir(cdir):
        for f in os.listdir(cdir):
            if f.endswith(".ogg") and f not in keep:
                os.remove(os.path.join(cdir, f))
    manifest = {"lang": lang, "era": era, "page": page, "title": ch["title"], "era_title": ch.get("era_title"),
                "provider": prov.name, "voices": {"narrador": prov.voice(lang, "narrador"),
                                                  "citacao": prov.voice(lang, "citacao")},
                "format": f"audio/ogg; codecs=opus; {BITRATE}bps", "tracks": tracks,
                "duration_s": round(sum(t["duration_s"] for t in tracks), 2),
                "generated_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())}
    os.makedirs(cdir, exist_ok=True)
    with open(mpath, "w", encoding="utf-8") as f:
        json.dump(manifest, f, ensure_ascii=False, indent=1)


def pending_chars(prov, ch, cfg):
    """Caracteres que ainda não estão no cache de segmentos (o que de fato seria cobrado)."""
    n = 0
    for t in ch["tracks"]:
        for s in t["segments"]:
            txt = seg_text(s, cfg)
            if not os.path.exists(os.path.join(CACHE, prov.name, sha(prov.key(txt, ch["lang"], s["speaker"])) + ".pcm")):
                n += len(txt)
    return n


def dry_run(prov, cfg, langs, era, page):
    print(f"Provedor: {prov.name}  (nenhuma chamada à API)\n")
    print(f"{'idioma':6} {'cap.':>5} {'faixas':>6} {'caracteres':>11} {'a gerar':>11} {'horas':>6} {'US$ total':>10} {'US$ a gerar':>11}")
    tot = [0, 0, 0, 0, 0.0, 0.0, 0.0]
    for lang in langs:
        chs = list(chapters([lang], era, page))
        if not chs:
            continue
        c = sum(ch["chars"] for ch in chs)
        pend = sum(pending_chars(prov, ch, cfg) for ch in chs)
        hours = c / CHARS_PER_MIN[lang] / 60
        hp = pend / CHARS_PER_MIN[lang] / 60
        if prov.name == "chirp":
            cost, cp = c / 1e6 * PRECO_CHIRP_MILHAO, pend / 1e6 * PRECO_CHIRP_MILHAO
        else:
            cost, cp = hours * PRECO_GEMINI_HORA, hp * PRECO_GEMINI_HORA
        nt = sum(len(ch["tracks"]) for ch in chs)
        print(f"{lang:6} {len(chs):5d} {nt:6d} {c:11,d} {pend:11,d} {hours:6.1f} {cost:10.2f} {cp:11.2f}")
        for i, v in enumerate([len(chs), nt, c, pend, hours, cost, cp]):
            tot[i] += v
    print(f"{'total':6} {tot[0]:5d} {tot[1]:6d} {tot[2]:11,d} {tot[3]:11,d} {tot[4]:6.1f} {tot[5]:10.2f} {tot[6]:11.2f}")
    if prov.name == "chirp":
        print(f"\nChirp 3 HD: US$ {PRECO_CHIRP_MILHAO:.0f}/milhão de caracteres; o 1º milhão de cada mês é grátis "
              f"(gerar um idioma por mês reduz bastante o custo).")
    else:
        print(f"\nGemini: estimativa de US$ {PRECO_GEMINI_HORA:.2f}/hora de áudio (duração estimada por "
              f"caracteres/minuto: {CHARS_PER_MIN}). Confira o preço atual do modelo {cfg['gemini']['model']}.")
    mb = tot[4] * 3600 * 48_000 / 8 / 1e6
    print(f"Tamanho estimado em Opus {BITRATE}: ~{mb:,.0f} MB (~{mb / 1024:.2f} GB).")


def sample(prov, cfg, lang, era, page, voices, max_chars=1400):
    ch = next(chapters([lang], era, page), None)
    if not ch:
        sys.exit("capítulo não encontrado nos roteiros (rode scripts.py antes).")
    # abertura + primeira faixa de texto, cortada em ~max_chars, + a primeira citação do capítulo (se houver)
    segs, size = [], 0
    for t in ch["tracks"]:
        for s in t["segments"]:
            if s["speaker"] == "narrador" and size < max_chars:
                segs.append(s)
                size += s["chars"]
    quote = next(([s, n] for t in ch["tracks"] for s, n in zip(t["segments"], t["segments"][1:])
                  if s["speaker"] == "citacao"), [])
    track = {"segments": segs + quote}
    cost = sum(s["chars"] for s in track["segments"]) * len(voices)
    print(f"Amostra {lang} era {era} cap {page}: {len(voices)} vozes x ~{cost // len(voices)} caracteres")
    for v in voices:
        pcm, _ = build_track_pcm(prov, track, lang, cfg, narrator_override=v)
        dest = os.path.join(OUT, "amostras", prov.name, lang, f"era{era}_cap{page}_{v.split('-')[-1]}.ogg")
        dur = encode_opus(pcm, prov.rate, dest, f"Amostra {v}")
        print(f"  {dest}  ({dur:.0f} s)")


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--provider", choices=PROVIDERS, default="chirp")
    ap.add_argument("--langs", default="pt,en,es")
    ap.add_argument("--era", type=int)
    ap.add_argument("--page", type=int)
    ap.add_argument("--dry-run", action="store_true", help="só calcula o custo estimado")
    ap.add_argument("--sample", nargs=3, metavar=("IDIOMA", "ERA", "PAGINA"), help="gera uma amostra curta")
    ap.add_argument("--voices", help="vozes da amostra separadas por vírgula (ex.: Charon,Iapetus,Kore)")
    ap.add_argument("--force", action="store_true", help="remonta todas as faixas (o cache de segmentos continua valendo)")
    args = ap.parse_args()

    cfg = load_json(VOZES)
    prov = PROVIDERS[args.provider](cfg)
    langs = args.langs.split(",")

    if args.dry_run:
        return dry_run(prov, cfg, langs, args.era, args.page)
    if args.sample:
        lang, era, page = args.sample[0], int(args.sample[1]), int(args.sample[2])
        voices = (args.voices or prov.voice(lang, "narrador")).split(",")
        return sample(prov, cfg, lang, era, page, [v.strip() for v in voices if v.strip()])
    for ch in chapters(langs, args.era, args.page):
        generate_chapter(prov, ch, cfg, force=args.force)
    print("pronto.")


if __name__ == "__main__":
    main()
