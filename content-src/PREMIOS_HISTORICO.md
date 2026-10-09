# Completar o histórico dos prêmios (todos os anos)

O app tem 7 prêmios (ids: 1 Oscar, 2 Globo de Ouro, 3 BAFTA, 4 Critics Choice, 5 SAG/Actor Awards,
6 Independent Spirit, 7 Cannes). Os anos recentes (2021–2026, e 2018–2020 em alguns) já estão prontos.
A tarefa é cobrir **todas as edições desde a primeira**, com **todos os vencedores** e, sempre que a fonte
listar, **todos os indicados**. Seja assertivo: cada vencedor precisa estar certo.

## Formato e ferramentas
- Um arquivo por ano: `content-src/pt/awards/<id>/<ano>.md` — formato no topo de `content-src/awards.py`
  (leia). Ano = ano da cerimônia (Cannes: ano do festival). Confira com
  `python3 content-src/awards.py check <id>` (rápido: cache em SQLite). **Não rode `build`.**
- Não mexa nos anos que já existem: os `.md` de 2021–2026 e os anos já no app
  (`android/app/src/main/assets/local/pt/awards/nominees/<id>/<ano>.json`).
- Fonte principal: as páginas de cada edição na Wikipédia em inglês (ex.: "1st Academy Awards",
  "1st Golden Globe Awards", "1st British Academy Film Awards", "1946 Cannes Film Festival"...), lidas
  pelo shell com `content-src/wiki.py` (biblioteca Python; leia o topo): `wikitext(página)` traz o
  wikitext, `links(trecho)` extrai os links e `resolve([artigos])` devolve o id do TMDB exato
  (artigo da Wikipédia → Wikidata → TMDB). Escreva um script seu (no scratchpad, não no repo) que lê
  as páginas do seu prêmio, identifica categorias, vencedor (geralmente em negrito `'''…'''` ou
  listado primeiro, conforme o padrão da página — confira!) e indicados, e gera os `.md`.
- Sem id no Wikidata: `python3 content-src/tmdb.py movie "Título" ano` / `person "Nome"` e confira
  ano/diretor; se não existir no TMDB, use `x: Título | Diretor`.
- Confira os vencedores contra uma segunda fonte (a página "Academy Award for Best Picture" etc. da
  Wikipédia, que lista os vencedores por ano, ou o site oficial) pelo menos nas categorias principais, por
  script. Diferença = investigar e corrigir.

## Categorias
- Use os nomes em português que o app já usa para esse prêmio (veja os `.md` de 2021+ da pasta) e dê nome
  em português às categorias históricas (ex.: "Best Unique and Artistic Picture" → "Melhor Produção
  Artística"; "Best Cinematography (Black-and-White)" → "Melhor Fotografia (Preto e Branco)"). Mantenha
  o mesmo nome para a mesma categoria em todos os anos. Ordem: a mesma dos anos recentes; históricas no fim.
- Só cinema (ignore TV). Prêmios honorários/especiais sem disputa: pode incluir como categoria com o
  vencedor (ex.: "Prêmio Honorário"), sem indicados.
- Pessoa x filme: atuação e direção = `p:ID @ m:ID`; o resto = filme (`m:ID`), com `[texto]` quando
  ajudar (ex.: `m:ID [Nome da canção]`). Roteiro: pessoa com filme se 1–2 autores (nome "A e B" com
  `p:ID "A e B" @ m:ID`), filme se mais.
- Sem `## text`/vídeos nos anos antigos (opcional só para edições marcantes, com fonte).

## Fontes
Liste as páginas usadas em `content-src/pt/awards/<id>/FONTES_HISTORICO_<faixa>.md` (`Título | URL`),
uma por linha — vão para a tela de Referências. Pode listar só as páginas principais (uma por década, se
forem muitas iguais: ex. "Wikipédia — 1st a 30th Academy Awards | https://en.wikipedia.org/wiki/1st_Academy_Awards").

Responda com um resumo curto: anos feitos, quantas categorias/indicados, como conferiu, o que ficou em
dúvida (liste ano + categoria).
