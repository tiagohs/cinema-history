#!/usr/bin/env python3
"""
Prêmios do app (indicados e vencedores por ano) a partir de arquivos de texto simples.

Cada ano de cada prêmio fica em content-src/pt/awards/<id>/<ano>.md (ids: 1 Oscar, 2 Globo de Ouro,
3 BAFTA, 4 Critics Choice, 5 SAG/Actor Awards, 6 Independent Spirit, 7 Cannes). O builder busca no TMDB
o título em português, o pôster, o diretor, o nome e a foto das pessoas, e grava
android/app/src/main/assets/local/pt/awards/nominees/nominees_<id>.json (os anos que não têm .md
continuam como estão no JSON).

    python3 content-src/awards.py check [id]     # confere tudo e mostra o resumo, sem gravar
    python3 content-src/awards.py build [id]     # grava o JSON do app
    python3 content-src/awards.py publish        # copia para o site (Cloudflare) + manifest; veja REMOTO.md

Formato do .md:

    year: 2025
    ## text
    Parágrafo opcional (HTML simples, {{m:ID}} e {{p:ID}} viram links).
    ## jury
    p:5655, p:1032, p:2963
    ## cat: Melhor Filme
    * m:1064213                      <- "*" = vencedor, "-" = indicado
    - m:549509
    ## cat: Melhor Ator
    * p:2037 @ m:1064213             <- pessoa + filme
    - p:1892 @ m:933260 [Diretor/Roteirista]   <- [texto] = função mostrada abaixo do nome
    - x: Nome do curta | Diretor     <- sem TMDB (sem link nem imagem)
    - m:583406 [Fight for You]       <- filme: [texto] aparece no lugar do diretor (ex.: canção)
    ## video: <youtube_id> | Título no app | Canal
    ## block: Título do destaque
    Texto do destaque.

Um "m:ID" pode levar um nome próprio: m:ID "Título" (usa esse título em vez do TMDB).
"""
import glob
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import build as B  # noqa: E402

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
SRC = os.path.join(HERE, "pt", "awards")
OUT = os.path.join(ROOT, "android/app/src/main/assets/local/pt/awards/nominees")

ENTRY = re.compile(
    r'^([*-])\s*(?:(m|p):(\d+)(?:\s+"([^"]+)")?(?:\s*@\s*m:(\d+)(?:\s+"([^"]+)")?)?|x:\s*([^|\[]+?)(?:\s*\|\s*([^\[]+?))?)\s*(?:\[([^\]]+)\])?\s*$')


def directors(mid):
    crew = (B.tmdb(f"/movie/{mid}/credits").get("crew") or [])
    names = []
    for c in crew:
        if c.get("job") == "Director" and c.get("name") not in names:
            names.append(c["name"])
    return ", ".join(names[:2]) or None


def movie_node(mid, custom, errors, winner=None):
    m = B.movie(mid)
    if not m or not m.get("id"):
        errors.append(f"filme {mid} não existe no TMDB")
        return None
    node = {"type": "movie", "id": int(mid), "name": custom or m.get("title") or m.get("original_title"),
            "image_path": m.get("poster_path") or B.movie(mid, "en-US").get("poster_path"),
            "director": directors(mid)}
    if winner is not None:
        node["winner"] = winner
    return {k: v for k, v in node.items() if v is not None or k == "winner"}


def person_node(pid, errors):
    p = B.person(pid)
    if not p or not p.get("id"):
        errors.append(f"pessoa {pid} não existe no TMDB")
        return None
    return {"type": "person", "id": int(pid), "name": p.get("name"), "image_path": p.get("profile_path")}


def parse_year(path, errors):
    lines = open(path, encoding="utf-8").read().splitlines()
    year, content, cur = None, [], None
    where = os.path.relpath(path, HERE)
    for n, raw in enumerate(lines, 1):
        line = raw.strip()
        if not line or line.startswith("<!--"):
            continue
        if line.startswith("year:"):
            year = line.split(":", 1)[1].strip()
            continue
        if line.startswith("## "):
            head = line[3:].strip()
            if head.startswith("cat:"):
                cur = {"type": "awards_nominees", "name": head[4:].strip(), "nominee_list": []}
            elif head.startswith("video:"):
                parts = [x.strip() for x in head[6:].split("|")]
                vid, title, channel = (parts + ["", "", ""])[:3]
                yt = B.youtube(vid)
                if not yt or "__error__" in yt:
                    errors.append(f"{where}:{n} vídeo {vid} não existe ou não permite incorporação")
                cur = {"type": "video", "video_id": vid,
                       "information": {"contentTitle": title, "source": f"Youtube: Canal <i>{channel}</i>"}}
            elif head == "text":
                cur = {"type": "text", "content_text": ""}
            elif head.startswith("block:"):
                cur = {"type": "block_special", "title": head[6:].strip(), "description": ""}
            elif head == "jury":
                cur = {"type": "person_list", "title": "O Júri", "persons": []}
            else:
                errors.append(f"{where}:{n} seção desconhecida: {head}")
                cur = None
                continue
            content.append(cur)
            continue
        if cur is None:
            errors.append(f"{where}:{n} linha fora de seção: {line}")
            continue
        t = cur["type"]
        if t == "awards_nominees":
            m = ENTRY.match(line)
            if not m:
                errors.append(f"{where}:{n} linha inválida: {line}")
                continue
            mark, kind, eid, custom, mov, mov_custom, xname, xdir, dept = m.groups()
            winner = mark == "*"
            if xname:
                node = {"type": "movie", "name": xname.strip(), "winner": winner}
                if xdir:
                    node["director"] = xdir.strip()
            elif kind == "m":
                node = movie_node(eid, custom, errors, winner)
                if node and dept:  # filme: [texto] substitui o diretor na linha de baixo (ex.: nome da canção)
                    node["director"] = dept.strip()
            else:
                node = person_node(eid, errors)
                if node:
                    if custom:
                        node["name"] = custom
                    node["winner"] = winner
                    if mov:
                        node["movie"] = movie_node(mov, mov_custom, errors)
                    if dept:
                        node["department"] = dept.strip()
            if node:
                cur["nominee_list"].append(node)
        elif t == "person_list":
            for pid in re.findall(r"p:(\d+)", line):
                p = person_node(pid, errors)
                if p:
                    cur["persons"].append({"id": p["id"], "name": p["name"], "profile_path": p["image_path"]})
        elif t == "text":
            cur["content_text"] = (cur["content_text"] + " " + B.render(line, errors)).strip()
        elif t == "block_special":
            cur["description"] = (cur["description"] + " " + B.render(line, errors)).strip()
        else:
            errors.append(f"{where}:{n} texto inesperado depois de um vídeo: {line}")
    if not year:
        errors.append(f"{where}: falta 'year:'")
    for c in content:
        if c["type"] == "awards_nominees":
            if not c["nominee_list"]:
                errors.append(f"{where}: categoria vazia: {c['name']}")
            elif not any(x.get("winner") for x in c["nominee_list"]):
                errors.append(f"{where}: categoria sem vencedor: {c['name']}")
    return year, content


def award_ids(arg):
    if arg:
        return [int(arg)]
    return sorted(int(os.path.basename(d)) for d in glob.glob(os.path.join(SRC, "*")) if os.path.basename(d).isdigit())


def build(aid, write):
    errors = []
    out_path = os.path.join(OUT, f"nominees_{aid}.json")
    current = json.load(open(out_path)) if os.path.exists(out_path) else []
    by_year = {y["year"]: y for y in current}
    srcs = sorted(glob.glob(os.path.join(SRC, str(aid), "[0-9][0-9][0-9][0-9].md")))
    for path in srcs:
        year, content = parse_year(path, errors)
        if year:
            by_year[year] = {"year": year, "content": content}
    years = sorted(by_year.values(), key=lambda y: y["year"], reverse=True)
    cats = sum(1 for y in years for c in y["content"] if c["type"] == "awards_nominees")
    print(f"prêmio {aid}: {len(srcs)} anos no texto, {len(years)} anos no app "
          f"({', '.join(y['year'] for y in years)}), {cats} categorias")
    for e in errors:
        print("   ERRO", e)
    if write and not errors:
        with open(out_path, "w", encoding="utf-8") as fh:
            json.dump(years, fh, ensure_ascii=False, indent=2)
            fh.write("\n")
    return not errors


def main():
    if len(sys.argv) < 2 or sys.argv[1] not in ("check", "build", "publish"):
        print(__doc__)
        return 1
    if sys.argv[1] == "publish":
        import remote
        return remote.publish()
    ok = all([build(a, sys.argv[1] == "build") for a in award_ids(sys.argv[2] if len(sys.argv) > 2 else None)])
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
