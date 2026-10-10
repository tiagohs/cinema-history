#!/usr/bin/env python3
"""
Envia os áudios gerados (out/<idioma>/...) para o bucket R2 cinema-history-audio, só o que ainda não foi enviado.

    CLOUDFLARE_API_TOKEN=... CLOUDFLARE_ACCOUNT_ID=... python3 content-src/audio/upload.py pt [--dry-run]

Usa a API da Cloudflare (PUT de objeto no R2, 8 envios em paralelo). As faixas têm o hash no nome, então só sobem as novas;
os manifest.json sobem sempre que mudam. O registro do que já subiu fica em out/.enviados.json.
Depois de enviar, apague do bucket as faixas antigas se quiser (não atrapalham: nenhum manifest aponta para elas).
"""
import hashlib
import json
import os
import sys
import urllib.request
from concurrent.futures import ThreadPoolExecutor

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
    base = (f"https://api.cloudflare.com/client/v4/accounts/{os.environ['CLOUDFLARE_ACCOUNT_ID']}"
            f"/r2/buckets/{BUCKET}/objects/")
    auth = {"Authorization": "Bearer " + os.environ["CLOUDFLARE_API_TOKEN"]}

    def put(item):
        key, path, ext, h = item
        err = None
        for _ in range(4):
            try:
                req = urllib.request.Request(base + key, data=open(path, "rb").read(), method="PUT",
                                             headers={**auth, "Content-Type": TYPES[ext]})
                with urllib.request.urlopen(req, timeout=120) as r:
                    if json.load(r).get("success"):
                        return item
            except Exception as e:  # noqa: BLE001 - tenta de novo
                err = e
        raise RuntimeError(f"falhou em {key}: {err}")

    tracks = [t for t in todo if t[2] != ".json"]
    manifests = [t for t in todo if t[2] == ".json"]
    done = 0
    # faixas primeiro, manifests depois (o app nunca vê um manifest apontando para faixa que ainda não subiu)
    for group in (tracks, manifests):
        with ThreadPoolExecutor(8) as pool:
            for key, _, _, h in pool.map(put, group):
                reg[key] = h
                done += 1
                if done % 50 == 0 or done == len(todo):
                    json.dump(reg, open(REG, "w"), indent=0, sort_keys=True)
                    print(f"  {done}/{len(todo)}", flush=True)
    json.dump(reg, open(REG, "w"), indent=0, sort_keys=True)
    return 0


if __name__ == "__main__":
    sys.exit(main())
