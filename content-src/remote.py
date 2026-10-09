#!/usr/bin/env python3
"""
Publica no site (Cloudflare Pages) os arquivos de conteúdo que o app atualiza sem nova versão.

    python3 content-src/remote.py [--site /home/claude/website]

Copia os arquivos de PUBLISH (para pt, en e es) de android/app/src/main/assets/local/<idioma>/ para
<site>/cinema-history/content/<idioma>/ e gera <site>/cinema-history/content/manifest.json com o sha256
de cada um. Depois é só fazer commit/push do site (o Cloudflare Pages publica sozinho).
O app (helpers/.../RemoteContent.kt) confere o manifest a cada 6 h e baixa só o que mudou.
"""
import glob
import hashlib
import json
import os
import shutil
import sys
import time

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
ASSETS = os.path.join(ROOT, "android/app/src/main/assets/local")
LANGS = ("pt", "en", "es")
# Arquivos atualizáveis pelo site (caminhos relativos a assets/local/<idioma>/). Para estender a outros
# conteúdos, acrescente aqui — o app já procura qualquer arquivo de conteúdo no cache remoto.
PUBLISH = ["awards.json", "awards/nominees/*/*.json", "awards/history/*.json"]
# Versão mínima do app (versionCode) que entende o conteúdo publicado.
MIN_APP_VERSION = 20


def publish(site=None):
    site = site or os.environ.get("SITE_DIR", "/home/claude/website")
    out = os.path.join(site, "cinema-history", "content")
    if os.path.isdir(out):
        shutil.rmtree(out)
    files = {}
    for lang in LANGS:
        for pattern in PUBLISH:
            for src in sorted(glob.glob(os.path.join(ASSETS, lang, pattern))):
                rel = f"{lang}/" + os.path.relpath(src, os.path.join(ASSETS, lang)).replace(os.sep, "/")
                data = open(src, "rb").read()
                json.loads(data)
                dst = os.path.join(out, rel)
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                open(dst, "wb").write(data)
                files[rel] = hashlib.sha256(data).hexdigest()
    manifest = {"format": 1, "min_app_version": MIN_APP_VERSION,
                "updated": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()), "files": files}
    with open(os.path.join(out, "manifest.json"), "w") as fh:
        json.dump(manifest, fh, indent=1, sort_keys=True)
    # o mesmo manifest vai dentro do app: o app só baixa o que for diferente do que já tem embutido
    with open(os.path.join(ASSETS, "remote_manifest.json"), "w") as fh:
        json.dump(manifest, fh, indent=1, sort_keys=True)
    print(f"{len(files)} arquivos publicados em {out}")
    return 0


if __name__ == "__main__":
    site = sys.argv[sys.argv.index("--site") + 1] if "--site" in sys.argv else None
    sys.exit(publish(site))
