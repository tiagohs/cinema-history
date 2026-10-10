#!/usr/bin/env python3
"""
Ajudante de pesquisa para quem escreve os capítulos.

    tmdb.py movie "Parasite" [ano]     -> id, título original, título BR, ano
    tmdb.py person "Bong Joon-ho"      -> id, nome, conhecido por
    tmdb.py videos <movie_id>          -> vídeos do YouTube do filme (trailers, featurettes)
    tmdb.py images <movie_id>          -> backdrops (file_path) para usar em image: tmdb_path=
    tmdb.py yt <youtube_id>...         -> confere se o vídeo existe e permite incorporação
"""
import sys
import urllib.parse

sys.path.insert(0, __import__("os").path.dirname(__file__))
import build as B  # noqa: E402


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        return
    cmd, arg = sys.argv[1], sys.argv[2]
    if cmd == "movie":
        params = {"query": arg, "language": "pt-BR"}
        if len(sys.argv) > 3:
            params["year"] = sys.argv[3]
        for m in (B.tmdb("/search/movie", **params).get("results") or [])[:8]:
            print(f"{m['id']:>8}  {m.get('original_title')} | BR: {m.get('title')} | {(m.get('release_date') or '')[:4]} | votos {m.get('vote_count')}")
    elif cmd == "person":
        for p in (B.tmdb("/search/person", query=arg).get("results") or [])[:6]:
            known = ", ".join((k.get("title") or k.get("name") or "") for k in p.get("known_for", [])[:3])
            print(f"{p['id']:>8}  {p.get('name')} ({p.get('known_for_department')}) — {known}")
    elif cmd == "videos":
        for lang in ("pt-BR", "en-US"):
            for v in (B.tmdb(f"/movie/{arg}/videos", language=lang).get("results") or []):
                if v.get("site") == "YouTube":
                    print(f"{lang}  {v['key']}  [{v.get('type')}] {v.get('name')}")
    elif cmd == "images":
        imgs = B.tmdb(f"/movie/{arg}/images", include_image_language="null,en")
        for b in (imgs.get("backdrops") or [])[:10]:
            print(f"{b['file_path']}  lang={b.get('iso_639_1')} votos={b.get('vote_average')}  https://image.tmdb.org/t/p/w780{b['file_path']}")
    elif cmd == "yt":
        for vid in sys.argv[2:]:
            r = B.youtube(vid)
            print(f"{vid}: " + (f"ERRO {r['__error__']}" if "__error__" in r else f"OK \"{r.get('title')}\" — {r.get('author_name')}"))


if __name__ == "__main__":
    main()
