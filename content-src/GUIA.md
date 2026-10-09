# Guia para escrever um capítulo do Cinema History

O app conta a história do cinema mundial em eras (partes). Cada era tem capítulos; cada capítulo é uma página
com texto corrido, vídeos com legenda, imagens, citações, "textos internos" (blocos destacados), a lista
"Vale a conferida…" (filmes) e a lista de pessoas. Tudo em **português do Brasil**.

Você escreve no formato de autoria (`content-src/pt/main_<era>/page_<n>.md`); o `build.py` transforma em JSON,
resolve os links do TMDB, baixa as imagens e confere os vídeos.

## Antes de escrever
1. Leia um capítulo existente para pegar o tom: `python3 content-src/build.py` não lê JSON antigo, então leia
   `android/app/src/main/assets/local/pt/pages/main_7/main_7_page_1.json` (terror 2010) e
   `.../main_7/main_7_page_10.json` (streaming 2010) — veja o tamanho dos parágrafos e o estilo.
2. Leia o relatório de pesquisa: `/home/claude/reports/Cinema mundial de 2020 até hoje.md` e as notas em
   `/home/claude/research_notes/Cinema mundial de 2020 até hoje/` (já tem fontes). **Confira na web tudo que for
   de 2024 em diante** (datas, números, resultados de prêmios, status de fusões): hoje é outubro de 2026.
3. Não repita o que os capítulos existentes já cobrem em detalhe (veja os títulos em
   `android/app/src/main/assets/local/pt/history_sumarios/hmt_sumarios_7.json`); cite e siga adiante.

## Estilo
- Tom de ensaio jornalístico, acessível, com opinião embasada; frases claras; sem jargão sem explicar.
- Parágrafos de 70 a 160 palavras. 8 a 12 parágrafos de texto no capítulo.
- Toda vez que citar um **filme** ou uma **pessoa** relevante pela primeira vez, use a tag de link:
  `{{m:ID}}` (filme) → vira `Título original (BR: Título no Brasil, ano)` com link; `{{p:ID}}` → nome com link.
  Use `{{m:ID|texto}}` quando o título já foi dito ou a frase pede outra forma (ex.: `{{m:496243|Parasita}}`).
  Depois da primeira menção, use o título em português em itálico: `<i>Parasita</i>`.
- Séries, empresas, eventos e termos em itálico (`<i>Netflix</i>`), destaques em `<strong>`.
- HTML permitido: `<i>`, `<strong>`, `<br/>`. Nada de markdown (`**`, `_`).
- Números em formato brasileiro: US$ 1,2 bilhão; 15 de março de 2020.
- Nunca invente fato, número, data, citação ou vídeo. Se não conseguir confirmar, não use.

## Elementos do capítulo (intercalados com o texto, seguindo o ritmo abaixo)
**Ritmo da página** (regras do autor, valem para todo capítulo):
1. A cada dois parágrafos de texto (`# text`) deve vir um elemento visual: imagem (`# image`), vídeo (`# video`
   ou `# essay`) ou citação (`# quote`). Nunca mais de dois parágrafos seguidos sem um deles (o `# block` não
   conta como elemento visual).
2. Varie: não repita o mesmo tipo de elemento em sequência na mesma página (ex.: vídeo e, logo depois, outro
   vídeo); alterne imagem, vídeo e citação. Evite também ensaio logo depois de vídeo (ou o contrário).
3. Logo no início do capítulo (depois do cabeçalho com título/subtítulo), entre o primeiro e o segundo
   parágrafo, deve vir um vídeo ou uma imagem.
4. Nunca coloque um texto interno (`# block`) e uma citação (`# quote`) colados, em qualquer ordem: sempre há
   pelo menos um parágrafo entre eles. O mesmo vale para quaisquer dois elementos especiais (block, quote,
   video, image, essay): sempre separados por parágrafo. Na prática, cabe no máximo um elemento entre dois
   parágrafos; não planeje mais elementos do que o capítulo tem de parágrafos.

- `# sumario` (obrigatório, no topo): `title` curto (até ~45 caracteres), `description` de 1 a 2 frases
  (aparece no índice), `image`.
- `# text`: um parágrafo por seção.
- `# video` (3 a 5): `youtube` (ID), `title`, `text` (1 a 2 frases de legenda: o que é e por que importa),
  `source` (`Youtube: Canal <i>Nome do canal</i>`). Prefira trailers oficiais, bastidores, entrevistas e
  reportagens de canais oficiais. Use `python3 content-src/tmdb.py videos <id_filme>` para achar vídeos
  oficiais e `python3 content-src/tmdb.py yt <ID>` para confirmar que o vídeo existe e pode ser incorporado.
- `# essay` (0 a 2): vídeo-ensaio (canais como Every Frame a Painting, Lessons from the Screenplay, Nerdwriter,
  Thomas Flight, Patrick (H) Willems, Like Stories of Old, Accented Cinema, Entre Planos...): `youtube`, `title`
  (título original do vídeo), `description` (resumo em português), `channel` (id curto, ex.: `thomas_flight`).
- `# quote` (1 a 2): frase curta (até ~25 palavras) traduzida para o português. Prefira declarações públicas de
  cineastas/atores (entrevistas, discursos de prêmio). Se usar fala de filme, só uma linha curta.
  `author` no formato `Nome (contexto, ano)` ou `Personagem em Filme (ano)`.
- `# block` (1 a 2, obrigatório pelo menos 1): o **texto interno** — um aprofundamento destacado de um
  tema específico (ex.: no capítulo de 1895 há "Os Filmes em Série"). `title`, `image`, `text` (120 a 220
  palavras, pode usar tags de link), `link` opcional (`URL | Texto do botão`) para uma fonte confiável.
- `# image` (0 a 2): `image`, `title`, `text` (legenda), `source`.
- `# movies` (obrigatório, 8 a 15 ids): a lista **Vale a conferida…** — os filmes essenciais do tema, na ordem
  em que devem aparecer.
- `# persons` (6 a 12 ids): as pessoas centrais do capítulo.
- Linhas começando com `//` são comentários (ignorados). **No fim do arquivo liste as fontes** usadas:
  `// fonte: Título — URL`.

## Imagens (`image:`)
`name=img_<nome_unico_descritivo>` + uma origem:
- `tmdb_movie=<id>`: usa o melhor backdrop sem texto do filme;
- `tmdb_path=/arquivo.jpg`: um backdrop específico (`python3 content-src/tmdb.py images <id>` lista e dá a URL);
- `url=https://upload.wikimedia.org/...`: foto livre do Wikimedia Commons (só com licença livre; anote em comentário).
Veja a imagem antes de escolher (baixe a URL w780 para o seu scratch com curl e abra com Read): sem texto,
sem cartaz, que represente o tema. Nomes únicos: comece com `img_` + tema (ex.: `img_covid_cinema_vazio`).

## Validar
`python3 content-src/build.py check content-src/pt/main_<era>/page_<n>.md` — mostra o texto final com os links
resolvidos (confira se o título BR e o ano batem com o que você quis citar!) e lista erros. Termine só com
**zero erros**. Não rode `build` (só `check`). Não mexa em outros arquivos.
