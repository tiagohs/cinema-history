#!/usr/bin/env python3
"""Gera a FONTE DE TESTE do áudio para builds de debug do app (sem TTS, sem API, custo zero).

Cria em android/app/src/debug/assets/audio-test/:
  tones/a.ogg, b.ogg, c.ogg            tons sintéticos curtos (Opus, ~12 kbps), compartilhados
  <idioma>/main_<era>/page_<n>/manifest.json
                                       manifest no MESMO formato do generate.py, montado a partir do
                                       roteiro real (roteiros/<idioma>/...), com todas as faixas do capítulo.
                                       Cada faixa aponta para um dos tons ("file": "../../../tones/b.ogg") e as
                                       "marks" (com source_index) são espalhadas pela duração do tom, proporcionais
                                       ao tamanho de cada segmento. Assim dá para testar o player, o destaque do
                                       parágrafo e o "Ouvir a partir daqui" no emulador.

Uso: python3 content-src/audio/make_test_assets.py [--chapters 1:1,1:2] [--langs pt,en,es]
Precisa de ffmpeg com libopus. Total gerado: bem abaixo de 300 KB.
"""
import argparse
import json
import os
import shutil
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
ROTEIROS = os.path.join(HERE, "roteiros")
DEST = os.path.join(ROOT, "android", "app", "src", "debug", "assets", "audio-test")

# nome -> (duração em segundos, frequência em Hz). O tom "a" é curto (abertura).
TONES = {"a": (12, 523), "b": (25, 392), "c": (25, 330)}


def make_tone(name, seconds, freq):
    dest = os.path.join(DEST, "tones", f"{name}.ogg")
    os.makedirs(os.path.dirname(dest), exist_ok=True)
    # tom suave com "pulsos" de 1 s (ajuda a perceber o avanço/retrocesso de 15 s)
    expr = f"0.12*sin(2*PI*{freq}*t)*(0.6+0.4*cos(2*PI*t))"
    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi",
                    "-i", f"aevalsrc={expr}:s=24000:d={seconds}",
                    "-c:a", "libopus", "-b:a", "12k", "-ac", "1", "-application", "audio",
                    "-metadata", f"title=Tom de teste {name}", dest], check=True)
    return os.path.getsize(dest)


def manifest_for(lang, era, page):
    path = os.path.join(ROTEIROS, lang, f"main_{era}", f"page_{page}.json")
    with open(path, encoding="utf-8") as f:
        ch = json.load(f)
    tracks = []
    for i, t in enumerate(ch["tracks"]):
        tone = "a" if i == 0 else ("b" if i % 2 else "c")
        dur = float(TONES[tone][0])
        total = max(1, sum(s["chars"] for s in t["segments"]))
        marks, acc = [], 0
        for s in t["segments"]:
            marks.append({"seg": s["id"], "t": round(0.25 + (dur - 1.0) * acc / total, 2),
                          "source_index": s.get("source_index", -1)})
            acc += s["chars"]
        tracks.append({"id": t["id"], "title": t["title"], "kind": t.get("kind"),
                       "file": f"../../../tones/{tone}.ogg", "duration_s": dur,
                       "bytes": os.path.getsize(os.path.join(DEST, "tones", f"{tone}.ogg")),
                       "chars": t["chars"], "hash": f"test-{tone}", "marks": marks})
    return {"lang": lang, "era": era, "page": page, "title": ch["title"], "era_title": ch.get("era_title"),
            "provider": "test", "voices": {}, "format": "audio/ogg; codecs=opus; 12kbps (teste)",
            "tracks": tracks, "duration_s": round(sum(t["duration_s"] for t in tracks), 2),
            "generated_at": "test"}


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--chapters", default="1:1,1:2", help="era:página separados por vírgula")
    ap.add_argument("--langs", default="pt,en,es")
    args = ap.parse_args()
    if not shutil.which("ffmpeg"):
        sys.exit("ffmpeg não encontrado no PATH.")
    if os.path.isdir(DEST):
        shutil.rmtree(DEST)
    size = sum(make_tone(n, d, f) for n, (d, f) in TONES.items())
    for lang in args.langs.split(","):
        for c in args.chapters.split(","):
            era, page = (int(x) for x in c.split(":"))
            m = manifest_for(lang, era, page)
            d = os.path.join(DEST, lang, f"main_{era}", f"page_{page}")
            os.makedirs(d, exist_ok=True)
            with open(os.path.join(d, "manifest.json"), "w", encoding="utf-8") as f:
                json.dump(m, f, ensure_ascii=False, indent=1)
            size += os.path.getsize(os.path.join(d, "manifest.json"))
    print(f"fonte de teste em {os.path.relpath(DEST, ROOT)} ({size / 1024:.0f} KB)")


if __name__ == "__main__":
    main()
