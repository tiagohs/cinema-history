#!/usr/bin/env python3
"""
Renderiza as artes da Google Play (v2) do app História do Cinema.

    python3 content/v2/render.py                  # tudo (3 idiomas)
    python3 content/v2/render.py --lang pt-BR     # só um idioma
    python3 content/v2/render.py --only 01,03     # só alguns screenshots
    python3 content/v2/render.py --size 1080x2160 # mais altos (máx. 2:1 pela Play; 1080x2340 é recusado)
    python3 content/v2/render.py --no-feature     # pula o feature graphic

Prints do app: procura primeiro em screens-raw/<idioma>/<nome>.png, depois em
screens-raw/<nome>.png e, se não achar, usa um print antigo de /screens (fallback),
recortando a barra de status/navegação antiga e desenhando uma barra de status limpa.

Requisitos: pip install playwright pillow  (Chromium em PLAYWRIGHT_BROWSERS_PATH=/opt/pw-browsers
ou `python3 -m playwright install chromium`).
"""
import argparse
import json
import os
import shutil
import sys
import tempfile
from pathlib import Path

from PIL import Image

V2 = Path(__file__).resolve().parent
REPO = V2.parent.parent
TEMPLATES = V2 / "templates"
RAW = V2 / "screens-raw"
OLD = REPO / "screens"
LANGS = ["pt-BR", "en-US", "es-419"]

# Prints novos (screens-raw) — recorte opcional em px (ex.: tirar barra de gestos).
# Se os prints novos já tiverem barra de status limpa (modo demo), deixe 0.
RAW_CROP_TOP = 0
RAW_CROP_BOTTOM = 0

# Prints antigos (1080x1920) têm barra de status (76px) e de navegação (144px).
OLD_CROP_TOP = 76
OLD_CROP_BOTTOM = 144

# nome do print novo -> substituto em /screens
FALLBACK = {
    "home": "home-1.png",
    "era": "historia-cinema.png",
    "capitulo": "historia-cinema-page-2.png",
    "capitulo-video": "historia-cinema-page-5.png",
    "premios": "premiacoes.png",
    "premios-ano": "premiacao-1.png",
    "timeline": "timeline.png",
    "1001": "1001-filmes-2.png",
    "filme": "filme.png",
    "pessoa": "ator-special.png",
    "glossario": "glossario-1.png",
    "referencias": "referencias-3.png",
}

# Um benefício por tela. Em "duo"/"trio" o ÚLTIMO celular fica na frente.
SLIDES = {
    "01": {"layout": "duo", "screens": ["era", "home"], "watermark": "1895"},
    "02": {"layout": "duo", "screens": ["capitulo-video", "capitulo"], "watermark": "1902"},
    "03": {"layout": "duo", "screens": ["premios-ano", "premios"], "watermark": "1929"},
    "04": {"layout": "single", "screens": ["timeline"], "watermark": "1927"},
    "05": {"layout": "single", "screens": ["1001"], "watermark": "1001"},
    "06": {"layout": "duo", "screens": ["pessoa", "filme"], "watermark": "1941"},
    "07": {"layout": "duo", "screens": ["referencias", "glossario"], "watermark": "A–Z"},
    "08": {"layout": "trio", "screens": ["home", "home", "home"], "watermark": "", "languages": True},
}

# anos de início das 8 eras (faixa de película do feature graphic)
ERA_YEARS = ["1895", "1930", "1940", "1960", "1970", "1990", "2010", "2020"]

LANG_DIR = {"PT": "pt-BR", "EN": "en-US", "ES": "es-419"}
EXTS = [".png", ".jpg", ".jpeg", ".webp"]


def find_raw(name, lang):
    for base in ([RAW / lang] if lang else []) + [RAW]:
        for ext in EXTS:
            p = base / f"{name}{ext}"
            if p.exists():
                return p
    return None


def prepare_screen(name, lang, workdir, log):
    """Devolve (caminho_da_imagem_pronta, precisa_statusbar)."""
    raw = find_raw(name, lang)
    if raw:
        src, top, bottom, statusbar = raw, RAW_CROP_TOP, RAW_CROP_BOTTOM, False
        log.append(f"    {name}: {raw.relative_to(V2)}")
    else:
        src = OLD / FALLBACK[name]
        top, bottom, statusbar = OLD_CROP_TOP, OLD_CROP_BOTTOM, True
        log.append(f"    {name}: FALLBACK screens/{FALLBACK[name]}")
    im = Image.open(src).convert("RGB")
    if top or bottom:
        im = im.crop((0, top, im.width, im.height - bottom))
    out = Path(workdir) / f"{lang}-{name}.png"
    im.save(out)
    return out, statusbar


def screenshot(page, html_path, cfg, out_png, w, h):
    page.set_viewport_size({"width": w, "height": h})
    page.add_init_script(f"window.CFG = {json.dumps(cfg, ensure_ascii=False)};")
    page.goto(html_path.as_uri())
    page.wait_for_selector("body[data-ready='1']", timeout=20000)
    tmp = out_png.with_suffix(".tmp.png")
    page.locator("#c").screenshot(path=str(tmp))
    # Play exige PNG 24 bits sem alfa
    Image.open(tmp).convert("RGB").save(out_png, optimize=True)
    tmp.unlink()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--lang", choices=LANGS, action="append")
    ap.add_argument("--only", help="ex.: 01,03")
    ap.add_argument("--size", default="1080x1920")
    ap.add_argument("--no-feature", action="store_true")
    ap.add_argument("--no-screens", action="store_true")
    args = ap.parse_args()

    w, h = (int(x) for x in args.size.lower().split("x"))
    if max(w, h) > 2 * min(w, h):
        sys.exit("Proporção inválida para a Play (lado maior no máximo 2x o menor).")
    langs = args.lang or LANGS
    only = set(args.only.split(",")) if args.only else None
    texts = json.loads((V2 / "textos" / "legendas.json").read_text(encoding="utf-8"))

    os.environ.setdefault("PLAYWRIGHT_BROWSERS_PATH", "/opt/pw-browsers")
    from playwright.sync_api import sync_playwright

    workdir = tempfile.mkdtemp(prefix="hc-store-")
    # cópia dos templates no diretório temporário (as páginas carregam fontes/CSS relativos)
    tpl = Path(workdir) / "templates"
    shutil.copytree(TEMPLATES, tpl)
    log = []
    try:
        with sync_playwright() as p:
            launch = {}
            exe = Path("/opt/pw-browsers/chromium")
            if exe.is_file():
                launch["executable_path"] = str(exe)
            try:
                browser = p.chromium.launch(**launch)
            except Exception:
                browser = p.chromium.launch()
            for lang in langs:
                t = texts[lang]
                log.append(f"[{lang}]")
                if not args.no_feature:
                    out = V2 / "feature-graphic" / f"{lang}.png"
                    out.parent.mkdir(parents=True, exist_ok=True)
                    page = browser.new_page(device_scale_factor=1)
                    cfg = dict(t["feature"], years=ERA_YEARS, highlight=["1895", "2020"])
                    screenshot(page, tpl / "feature.html", cfg, out, 1024, 500)
                    page.close()
                    log.append(f"  feature-graphic/{lang}.png")
                if args.no_screens:
                    continue
                for sid, slide in SLIDES.items():
                    if only and sid not in only:
                        continue
                    phones = []
                    for i, name in enumerate(slide["screens"]):
                        chip = None
                        slang = lang
                        if slide.get("languages"):
                            code = t["languages"][i]
                            slang = LANG_DIR[code]
                            chip = code
                        img, sb = prepare_screen(name, slang, workdir, log)
                        phones.append({"src": img.as_uri(), "statusbar": sb, "chip": chip})
                    if slide.get("languages"):
                        # na ordem do layout trio: [esq, dir, centro(frente)] -> idioma da loja no centro
                        phones = [phones[1], phones[2], phones[0]]
                    cfg = dict(t["slides"][sid], w=w, h=h, layout=slide["layout"],
                               watermark=slide["watermark"], phones=phones)
                    out = V2 / "screenshots" / lang / f"{sid}.png"
                    out.parent.mkdir(parents=True, exist_ok=True)
                    page = browser.new_page(device_scale_factor=1)
                    screenshot(page, tpl / "screenshot.html", cfg, out, w, h)
                    page.close()
                    log.append(f"  screenshots/{lang}/{sid}.png")
            browser.close()
    finally:
        shutil.rmtree(workdir, ignore_errors=True)
    print("\n".join(log))


if __name__ == "__main__":
    main()
