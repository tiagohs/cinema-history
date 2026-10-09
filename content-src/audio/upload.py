#!/usr/bin/env python3
"""
Envia os áudios gerados (out/<idioma>/...) para o bucket R2 cinema-history-audio, só o que ainda não foi enviado.

    CLOUDFLARE_API_TOKEN=... CLOUDFLARE_ACCOUNT_ID=... python3 content-src/audio/upload.py pt [--dry-run]

Usa o wrangler (npx wrangler r2 object put). As faixas têm o hash no nome, então só sobem as novas;
os manifest.json sobem sempre que mudam. O registro do que já subiu fica em out/.enviados.json.
Depois de enviar, apague do bucket as faixas antigas se quiser (não atrapalham: nenhum manifest aponta para elas).
"""
import hashlib
import json
import os
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "out")
BUCKET = "cinema-history-audio"
REG = os.path.join(OUT, ".enviados.json")
TYPES = {".ogg": "audio/ogg", ".json": "application/json; charset=utf-8"}


def sha(path):
    return hashlib.sha256(open(path, "rb").read()).hexdigest()


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    dry = "--dry-run" in sys.argv
    if not args:
        print(__doc__)
        return 1
    for var in ("CLOUDFLARE_API_TOKEN", "CLOUDFLARE_ACCOUNT_ID"):
        if not dry and not os.environ.get(var):
            sys.exit(f"Defina {var}.")
    reg = json.load(open(REG)) if os.path.exists(REG) else {}
    todo = []
    for lang in args:
        base = os.path.join(OUT, lang)
        for root, _, files in os.walk(base):
            if "amostras" in root.split(os.sep):
                continue
            for f in sorted(files):
                ext = os.path.splitext(f)[1]
                if ext not in TYPES:
                    continue
                path = os.path.join(root, f)
                key = os.path.relpath(path, OUT).replace(os.sep, "/")
                h = sha(path)
                if reg.get(key) != h:
                    todo.append((key, path, ext, h))
    # faixas primeiro, manifests por último: o app nunca vê um manifest apontando para faixa que ainda não subiu
    todo.sort(key=lambda x: x[2] == ".json")
    mb = sum(os.path.getsize(p) for _, p, _, _ in todo) / 1e6
    print(f"{len(todo)} arquivos para enviar ({mb:,.1f} MB)")
    if dry:
        return 0
    for i, (key, path, ext, h) in enumerate(todo, 1):
        cmd = ["npx", "--yes", "wrangler@4", "r2", "object", "put", f"{BUCKET}/{key}", "--file", path,
               "--content-type", TYPES[ext], "--remote"]
        r = subprocess.run(cmd, capture_output=True, text=True)
        if r.returncode != 0:
            print(r.stdout[-500:], r.stderr[-1500:])
            sys.exit(f"falhou em {key}")
        reg[key] = h
        if i % 20 == 0 or i == len(todo):
            json.dump(reg, open(REG, "w"), indent=0, sort_keys=True)
            print(f"  {i}/{len(todo)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
