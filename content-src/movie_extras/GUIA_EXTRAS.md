# Seções extras da tela de filme

Este guia explica como manter as seções "Você sabia?", citação, "Crítica e reviews" e
"Onde assistir → YouTube" da `MovieDetailsActivity`.

## De onde o app lê

`android/app/src/main/assets/local/<lang>/specials/movies.json`: lista de grupos
(`DECADE N` da lista 1001 e `HISTORY <era>` para filmes que só aparecem em "Vale a conferida"),
cada um com `movies: [{id, did_you_know_list, quote, review_results, watchOn, ...}]`.
O presenter encontra o filme pelo **id do TMDB** em qualquer grupo.

- `review_results`: agrupado por **idioma** (`"language": "pt" | "en" | "es"`). O app mostra
  "Português / Inglês / Espanhol" e começa pelo idioma da interface.
- `watchOn`: `{"type": "youtube_free", "link": ...}` vem primeiro quando existe filme completo
  legal no YouTube. `review_results` e `watchOn` não são traduzidos: o `content-i18n` copia as
  críticas e regionaliza `watchOn` por país (`regional.py`), mantendo `youtube_free` em todos.

`pt` é a fonte. `en`/`es` são gerados pelo `content-i18n`.

## Fluxo completo

Na raiz do repositório:

```bash
python3 content-src/movie_extras/movie_extras.py index          # ids (lista 1001 + "Vale a conferida") -> index.json
python3 content-src/movie_extras/movie_extras.py reviews        # críticas novas -> reviews.json
python3 content-src/movie_extras/movie_extras.py youtube        # YouTube -> youtube.json
python3 content-src/movie_extras/movie_extras.py todo --n 30    # próximos filmes sem curiosidades
#   ... escrever curated/bNNN.json (formato abaixo) ...
python3 content-src/movie_extras/movie_extras.py apply          # pt/specials/movies.json + references.json
                                                                # + content-i18n/work/{en,es}-movie-extras.json
python3 content-i18n/i18n.py watch                              # provedores (TMDB) dos filmes novos
python3 content-i18n/i18n.py watch-pt
for l in en es; do
  python3 content-i18n/i18n.py import --lang $l content-i18n/work/$l-movie-extras.json
  python3 content-i18n/i18n.py apply --lang $l
  python3 content-i18n/i18n.py validate --lang $l
done
cd android && ./gradlew -q :app:assembleDebug :app:lintVitalRelease
```

`reviews` e `youtube` só processam filmes que ainda não estão nos `.json` (use `--refresh` ou
`--ids 1,2,3` para refazer). Respostas HTTP ficam em `cache/` (ignorado no git).

## Curiosidades e citações (`curated/*.json`)

Escritas à mão, em pt/en/es juntos, um arquivo por lote:

```json
{
 "770": {
  "src": ["https://en.wikipedia.org/wiki/Gone_with_the_Wind_(film)"],
  "f": [["Título pt", "Texto pt", "Title en", "Text en", "Título es", "Texto es"]],
  "q": ["Citação pt", "Autor pt", "Quote en", "Author en", "Cita es", "Autor es"]
 },
 "12345": {"src": [], "f": [], "skip": "sem fonte confiável acessível"}
}
```

Regras:

- 2 a 4 fatos por filme, curtos (1 frase), verificáveis na fonte listada em `src`
  (em geral o artigo da Wikipedia em inglês; para filmes brasileiros, a Wikipédia em pt).
- Nada de boato: quando o fato é contado mas não comprovado, escreva "teria", "segundo relatos".
- Citação só com fonte confiável: falas da lista do AFI (100 Movie Quotes) ou frases de
  diretores/críticos documentadas. Na dúvida, sem citação.
- Sem fonte acessível: use `"skip"` em vez de inventar.
- `python3 movie_extras.py wiki --ids 1,2 --fetch` imprime trechos da Wikipedia para conferir
  (a API limita requisições; o padrão é ler só do cache).

## Críticas

| idioma | fontes | regra |
|---|---|---|
| pt | Plano Crítico, Vertentes do Cinema (API WordPress) | título + ano conferidos no texto |
| en | Rotten Tomatoes (página do filme → link do veículo) | só links que respondem 200 ou com cópia no Wayback |
| es | Encadenados, Miradas de Cine, El Antepenúltimo Mohicano | título conferido; ano quando disponível |

O trecho (≤ 25 palavras) é tirado do próprio texto da crítica. Para ampliar o espanhol,
novas fontes com API WordPress podem entrar em `collect_reviews` (`wp_reviews(site, ...)`).

## YouTube

`find_youtube` busca o filme e só aceita:

- domínio público nos EUA por idade (publicado até 1930 em 2026: regra de 95 anos — a
  constante sobe um ano a cada 1º de janeiro), ou listado em
  "List of films in the public domain in the United States" (`pd_us_titles.json`); ou
- canal oficial/arquivo em `OFFICIAL_CHANNELS` (Mosfilm, Shout! Studios, Library of Congress…).

Também confere a duração (contra o runtime do TMDB), o ano no título e exclui audiolivros,
trailers e "parte N". Vídeos aprovados automaticamente mas errados vão em `REJECTED_VIDEOS`
(id do TMDB → motivo). Revise os novos matches antes de publicar.

## Referências

`apply` recria em `pt/references.json` o grupo "Curiosidades e críticas" (`EXTRA_REFS` no
script) e grava as traduções no catálogo junto com as curiosidades.
