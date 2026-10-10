# Revisão dos capítulos

Cada capítulo (`content-src/pt/main_<era>/page_<n>.md`) passa por três revisões independentes. Você faz UMA delas
(o pedido diz qual). Leia antes `content-src/GUIA.md` (formato e estilo).

Regras para todas as revisões:
- Edite o arquivo diretamente, preservando o formato (seções `# tipo`, `chave: valor`, tags `{{m:ID}}`/`{{p:ID}}`,
  HTML só `<i>`, `<strong>`, `<br/>`). Não troque ids sem confirmar no TMDB (`python3 content-src/tmdb.py movie "Título" ano`).
- Ao terminar, rode `python3 content-src/build.py check <arquivo>`: precisa dar **zero erros**. Leia a saída: é o
  texto como o leitor vai ver (os links viram "Título original (BR: Título, ano)").
- Não mexa em outros arquivos. Não reescreva por gosto: mude o que melhora de verdade.
- No fim do arquivo acrescente uma linha de registro: `// revisão <tipo>: <resumo do que mudou>`.
- Responda com a lista de mudanças feitas (antes → depois, curto) e o que ficou pendente/duvidoso.

## Revisão 1 — fatos
Confira, com busca na web (fontes confiáveis: Variety, THR, Deadline, Box Office Mojo, The Numbers, sites oficiais
de prêmios/festivais, Ancine/Filme B, AP, Reuters, BBC, Folha, G1, Omelete...), cada afirmação verificável: datas,
números, prêmios, quem fez o quê, citações (autor, contexto e se a frase existe). Hoje é outubro de 2026.
- Corrija o que estiver errado; se não confirmar algo, suavize ou remova.
- Confira se cada `{{m:ID}}` resolve para o filme certo (título BR e ano na saída do check) e cada `{{p:ID}}` para a pessoa certa.
- Confira os vídeos: `python3 content-src/tmdb.py yt <ID>` mostra título e canal reais; a legenda (`title`/`text`/`source`)
  tem que bater com o vídeo. Se não bater, corrija a legenda ou troque o vídeo (confirmando o novo).
- Confira se as listas `# movies` e `# persons` fazem sentido para o capítulo.

## Revisão 2 — língua
Português do Brasil padrão: ortografia (Acordo de 1990), acentuação, concordância verbal e nominal, regência, crase,
pontuação, colocação pronominal, uso de maiúsculas, repetição de palavras, frases longas demais. Padronize:
títulos em itálico depois da primeira menção, números (US$ 1,2 bilhão; 15 de março de 2020), aspas (“ ”), travessões (—).
Não mude fatos.

## Revisão 3 — sentido e leitura
Leia como o leitor do app: o capítulo tem começo, meio e fim? Cada parágrafo faz sentido e se liga ao anterior?
Há contradições, saltos, repetição de ideias, frases vagas ou "enchimento"? Os textos internos (`# block`) aprofundam
de verdade? As legendas explicam por que o vídeo/imagem importa? O tom está coerente com o resto do app?
Confira também a coerência com os outros capítulos da mesma era (leia os títulos/sumários dos outros `.md` da pasta)
e corrija sobreposições. Faça ainda uma última passada de ortografia.
