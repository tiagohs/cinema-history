#!/usr/bin/env python3
"""
Busca no YouTube pela linha de comando (sem API) e mostra só vídeos que existem e permitem incorporação.

    python3 content-src/yt_search.py "nosferatu 1922 trailer"
"""
import json
import re
import subprocess
import sys
import urllib.parse

UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126 Safari/537.36"


def search(q):
    url = "https://www.youtube.com/results?hl=pt-BR&search_query=" + urllib.parse.quote(q)
    html = subprocess.run(["curl", "-s", "-A", UA, "-H", "Accept-Language: pt-BR,pt;q=0.9", url],
                          capture_output=True, text=True).stdout
    m = re.search(r"var ytInitialData = (\{.*?\});</script>", html)
    if not m:
        return []
    data = json.loads(m.group(1))
    out = []

    def walk(n):
        if isinstance(n, dict):
            if "videoRenderer" in n:
                v = n["videoRenderer"]
                out.append({
                    "id": v.get("videoId"),
                    "title": "".join(r.get("text", "") for r in v.get("title", {}).get("runs", [])),
                    "channel": "".join(r.get("text", "") for r in v.get("ownerText", {}).get("runs", [])),
                    "duration": v.get("lengthText", {}).get("simpleText", ""),
                    "views": v.get("viewCountText", {}).get("simpleText", ""),
                    "published": v.get("publishedTimeText", {}).get("simpleText", ""),
                })
            for x in n.values():
                walk(x)
        elif isinstance(n, list):
            for x in n:
                walk(x)

    walk(data)
    return out


def embeddable(vid):
    url = "https://www.youtube.com/oembed?format=json&url=" + urllib.parse.quote(f"https://www.youtube.com/watch?v={vid}")
    return subprocess.run(["curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", url],
                          capture_output=True, text=True).stdout == "200"


if __name__ == "__main__":
    for r in search(" ".join(sys.argv[1:]))[:12]:
        ok = "OK " if embeddable(r["id"]) else "XX "
        print(f"{ok}{r['id']}  {r['title']}  | {r['channel']} | {r['duration']} | {r['views']} | {r['published']}")
