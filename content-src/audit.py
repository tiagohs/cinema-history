#!/usr/bin/env python3
"""
Auditoria do conteúdo (assets/local/pt): imagens, vídeos do YouTube e links externos.
Gera content-src/audit/report.json e um resumo no terminal.

    python3 content-src/audit.py
"""
import concurrent.futures as cf
import json
import os
import re
import subprocess
import sys
import urllib.parse

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
ASSETS = os.path.join(ROOT, "android/app/src/main/assets/local/pt")
RES = os.path.join(ROOT, "android/app/src/main/res")
OUT = os.path.join(HERE, "audit")
MEDIA = "https://website-cb5.pages.dev/cinema-history/media/"  # = MediaUrls.BASE_URL no app
UA = "CinemaHistoryContentAudit/1.0 (https://github.com/tiagohs/cinema-history)"
os.makedirs(OUT, exist_ok=True)

drawables = set()
for d in os.listdir(RES):
    if d.startswith("drawable") or d == "raw":
        drawables |= {os.path.splitext(f)[0] for f in os.listdir(os.path.join(RES, d))}

images, videos, links = [], [], []
HREF = re.compile(r'href=\\?"(https?://[^"\\]+)')


def walk(node, where, ctx):
    if isinstance(node, dict):
        if "image_type" in node and "url" in node:
            images.append({"file": where, "type": node["image_type"], "url": node["url"], "ctx": ctx})
        for k in ("video_id",):
            if k in node and node[k]:
                videos.append({"file": where, "id": node[k], "ctx": node.get("information", {}).get("contentTitle") or node.get("title") or ctx})
        if node.get("type") == "gif":
            pass
        params = node.get("parameters")
        if isinstance(params, list):
            for p in params:
                if isinstance(p, dict) and isinstance(p.get("value"), str) and p["value"].startswith("http"):
                    links.append({"file": where, "url": p["value"], "ctx": node.get("button_text") or node.get("title") or ctx})
        if isinstance(node.get("link"), str) and node["link"].startswith("http"):
            links.append({"file": where, "url": node["link"], "ctx": node.get("title") or ctx})
        title = node.get("title") or node.get("name") or ctx
        for v in node.values():
            if isinstance(v, str):
                for u in HREF.findall(v):
                    if not u.startswith("https://_"):
                        links.append({"file": where, "url": u, "ctx": title if isinstance(title, str) else ctx})
                ym = re.findall(r"youtube\.com/(?:watch\?v=|embed/)([\w-]{11})|youtu\.be/([\w-]{11})|img\.youtube\.com/vi/([\w-]{11})", v)
                for g in ym:
                    vid = next(x for x in g if x)
                    videos.append({"file": where, "id": vid, "ctx": title if isinstance(title, str) else ctx})
            else:
                walk(v, where, title if isinstance(title, str) else ctx)
    elif isinstance(node, list):
        for v in node:
            walk(v, where, ctx)


for dp, _, fs in os.walk(ASSETS):
    for f in fs:
        if f.endswith(".json"):
            path = os.path.join(dp, f)
            walk(json.load(open(path)), os.path.relpath(path, ASSETS), "")


def curl(url, method="GET"):
    args = ["curl", "-s", "-o", "/dev/null", "-w", "%{http_code} %{url_effective}", "-L", "--max-time", "25",
            "-A", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/126 Mobile Safari/537.36"]
    if method == "HEAD":
        args.append("-I")
    try:
        out = subprocess.run(args + [url], capture_output=True, text=True, timeout=40).stdout.strip()
        code, _, final = out.partition(" ")
        return int(code or 0), final
    except Exception:
        return 0, ""


def check_link(url):
    code, final = curl(url, "HEAD")
    if code in (0, 403, 405, 400, 429) or code >= 500:
        code, final = curl(url)
    return code, final


def check_video(vid):
    url = "https://www.youtube.com/oembed?format=json&url=" + urllib.parse.quote(f"https://www.youtube.com/watch?v={vid}")
    code, _ = curl(url)
    return code


def main():
    report = {}
    # imagens
    img_status = []
    for im in images:
        st = "ok"
        if im["type"] == "local":
            st = "ok" if im["url"] in drawables else "faltando no app"
        img_status.append(im | {"status": st})
    online = [i for i in img_status if i["type"] in ("online", "online_firebase")]
    url = lambda i: MEDIA + i["url"].lstrip("/") if i["type"] == "online_firebase" else i["url"]
    with cf.ThreadPoolExecutor(16) as ex:
        for im, (code, _) in zip(online, ex.map(lambda i: check_link(url(i)), online)):
            im["status"] = "ok" if code == 200 else f"HTTP {code}"
    report["images"] = img_status

    # vídeos
    uniq = sorted({v["id"] for v in videos})
    with cf.ThreadPoolExecutor(16) as ex:
        codes = dict(zip(uniq, ex.map(check_video, uniq)))
    report["videos"] = [v | {"status": "ok" if codes[v["id"]] == 200 else f"HTTP {codes[v['id']]}"} for v in videos]

    # links
    ul = sorted({l["url"] for l in links})
    with cf.ThreadPoolExecutor(16) as ex:
        res = dict(zip(ul, ex.map(check_link, ul)))
    report["links"] = [l | {"status": "ok" if res[l["url"]][0] in (200, 201, 202, 204) else f"HTTP {res[l['url']][0]}",
                            "final": res[l["url"]][1]} for l in links]

    json.dump(report, open(os.path.join(OUT, "report.json"), "w"), ensure_ascii=False, indent=1)
    for k in ("images", "videos", "links"):
        items = report[k]
        bad = [i for i in items if i["status"] != "ok"]
        print(f"{k}: {len(items)} referências, {len(bad)} com problema")
        by = {}
        for b in bad:
            by[b["status"]] = by.get(b["status"], 0) + 1
        for s, n in sorted(by.items(), key=lambda x: -x[1]):
            print(f"   {n:4d}  {s}")


if __name__ == "__main__":
    sys.exit(main())
