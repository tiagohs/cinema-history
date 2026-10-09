# Completar os prêmios do app

O app Cinema History tem uma área de Prêmios (Oscar, Globo de Ouro, BAFTA, Critics Choice, SAG/Actor Awards,
Independent Spirit, Cannes). Para cada ano há as categorias com indicados e vencedores, alguns vídeos e,
às vezes, um texto. Hoje só existem os anos 2018–2020. A tarefa é completar os anos que faltam até a edição
mais recente (2026), com dados verificados.

## Onde escrever

Um arquivo por ano: `content-src/pt/awards/<id>/<ano>.md` (ids: 1 Oscar, 2 Globo de Ouro, 3 BAFTA,
4 Critics Choice, 5 SAG/Actor Awards, 6 Independent Spirit, 7 Cannes). O formato está no topo de
`content-src/awards.py` (leia). Confira com `python3 content-src/awards.py check <id>` até não haver ERRO.
**Não rode `build` e não edite nada fora de `content-src/pt/awards/<id>/`.**

O ano é o **ano da cerimônia** (ex.: Oscar 2020 = cerimônia de fevereiro de 2020, em que Parasita venceu).
Para Cannes, o ano do festival.

## Regras

1. **Dados só de fontes confiáveis**: o site oficial do prêmio (oscars.org/awardsdatabase, goldenglobes.com,
   bafta.org, criticschoice.com, sagawards.org / theactorawards.org, filmindependent.org, festival-cannes.com)
   e a Wikipédia (en/pt) para conferir. Não escreva indicado ou vencedor de memória: confirme cada categoria.
   Use WebFetch/WebSearch.
2. **Categorias**: use os mesmos nomes de categoria que o app já usa para esse prêmio (veja
   `android/app/src/main/assets/local/pt/awards/nominees/nominees_<id>.json`, ano mais recente), na mesma
   ordem. Categorias novas que passaram a existir (ex.: Oscar de Melhor Elenco/Escalação a partir de 2026,
   Globo de Ouro de Conquista Cinematográfica e de Bilheteria a partir de 2024) entram com nome em português.
   Categorias que deixaram de existir saem. Só cinema (o Globo de Ouro e o SAG também premiam TV: ignore TV).
3. **Quem é pessoa e quem é filme** (siga o app): atuação e direção = pessoa com o filme (`p:ID @ m:ID`);
   roteiro: pessoa com o filme também (como nas categorias de roteiro do Oscar 2020); todas as outras = filme
   (`m:ID`). Quando a categoria premia várias pessoas (ex.: roteiro com dois autores), use a primeira pessoa
   creditada ou o filme (`m:ID`) — prefira o filme se forem mais de duas. Revelação/homenagem = pessoa sem filme.
4. **IDs do TMDB**: `python3 content-src/tmdb.py movie "Título" [ano]` e `python3 content-src/tmdb.py person "Nome"`.
   Confira ano e diretor para não pegar o filme errado (remakes, homônimos). Curtas que não estão no TMDB:
   `x: Título | Diretor`. O builder busca título em português, pôster, diretor e fotos.
5. **Vídeos**: 1 a 3 por ano, de canais oficiais (Oscars, BAFTA, Golden Globes, Festival de Cannes, Critics
   Choice, SAG Awards, Film Independent, emissoras). Busque com `python3 content-src/yt_search.py "..."` (só use
   os marcados OK) e confira com `python3 content-src/tmdb.py yt <ID>`. Título no app em português (ex.:
   "Anora vence o Oscar de Melhor Filme"); canal = nome real do canal.
6. **Texto (opcional, recomendado)**: `## text` com 2 a 4 frases sobre a edição (data, local, apresentador,
   destaques, recordes), em português do Brasil, com links `{{m:ID}}`/`{{p:ID}}` na primeira menção de filmes
   e pessoas. Só fatos verificados. Pode haver um `## block: Título` com uma curiosidade da edição.
7. **Revise** cada arquivo: vencedor certo, nenhum indicado faltando ou sobrando (conte), ortografia e
   concordância do texto.
8. **Fontes**: liste em `content-src/pt/awards/<id>/FONTES.md` as páginas que você usou, uma por linha:
   `Título da página | URL` (vão para a tela de Referências do app).
9. **História** (opcional): se houve mudança importante no prêmio nesses anos (ex.: fim da HFPA no Globo de
   Ouro, SAG Awards renomeado para Actor Awards, novas categorias, regras de inclusão do Oscar), escreva 1 a 3
   parágrafos curtos em `content-src/pt/awards/<id>/HISTORIA.md`, com a fonte de cada um.

Responda com um resumo curto: anos e categorias feitos, o que ficou em dúvida e o que não achou.
