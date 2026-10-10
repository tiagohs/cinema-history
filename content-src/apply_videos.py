#!/usr/bin/env python3
"""Aplica content-src/audit/videos_map_*.json ao conteúdo pt (video_id, fonte, título; miniaturas img.youtube).
Faz substituição textual para não reformatar os arquivos."""
import glob, json, os, re
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PT = os.path.join(ROOT, "android/app/src/main/assets/local/pt")
maps = {}
for f in sorted(glob.glob(os.path.join(ROOT, "content-src/audit/videos_map_*.json"))):
    for m in json.load(open(f)):
        if m.get("new"):
            maps[m["old"]] = m
done = {"video": 0, "thumb": 0, "title": 0}
enc = lambda s: json.dumps(s, ensure_ascii=False)
DEC = json.JSONDecoder()
STR = r'"(?:[^"\\]|\\.)*"'


def fix_block(block, m):
    """block = texto do objeto do vídeo a partir de "video_id" até o fim do "information"."""
    block = re.sub(r'("source"\s*:\s*)' + STR, lambda mo: mo.group(1) + enc(m["source"]), block, count=1)
    if m.get("caption_title"):
        block, n = re.subn(r'("contentTitle"\s*:\s*)' + STR, lambda mo: mo.group(1) + enc(m["caption_title"]), block, count=1)
        done["title"] += n
    return block


for f in glob.glob(os.path.join(PT, "**/*.json"), recursive=True):
    raw = open(f).read()
    if not any(o in raw for o in maps):
        continue
    out, pos = [], 0
    for mo in re.finditer(r'"video_id"\s*:\s*"([\w-]{11})"', raw):
        old = mo.group(1)
        if old not in maps:
            continue
        m = maps[old]
        # o item do vídeo: do "{" que abre o objeto até o fim dele (decodificado de verdade)
        a = raw.rfind("{", 0, mo.start())
        _, b = DEC.raw_decode(raw, a)
        seg = raw[a:b]
        seg = seg.replace(mo.group(0), mo.group(0).replace(old, m["new"]))
        seg = fix_block(seg, m)
        out.append(raw[pos:a]); out.append(seg); pos = b
        done["video"] += 1
    out.append(raw[pos:])
    raw2 = "".join(out)

    def sub(mo):
        o = mo.group(1)
        if o in maps:
            done["thumb"] += 1
            return f"img.youtube.com/vi/{maps[o]['new']}/"
        return mo.group(0)
    raw2 = re.sub(r"img\.youtube\.com\\?/vi\\?/([\w-]{11})\\?/", sub, raw2)
    json.loads(raw2)
    open(f, "w").write(raw2)
print(done)
