# Pesquisa: como os apps de topo montam a página da Google Play

Data: outubro de 2026. Objetivo: basear a nova página do **História do Cinema** (v2.5.0) no que funciona nos apps de topo e nas regras atuais da Google Play.

## 1. Regras oficiais da Google Play (o que é obrigatório ou proibido)

**Feature graphic (1024 × 500)**
- JPEG ou PNG de 24 bits **sem transparência**.
- O que importa (nome, slogan) fica **no centro**. As bordas podem ser cortadas e recebem botões por cima, então ali só vai fundo.
- **Evitar fundo branco, preto ou cinza-escuro puro**, porque somem no fundo da loja. Usamos um marrom/vinho escuro.
- **Evitar imagem de aparelho**, selos da Play ou de outras lojas, logos e personagens de terceiros e qualquer coisa parecida com o ícone do app.
- Proibido: "Melhor", "nº 1", "Top", "Novo", "Grátis", "Promoção", "1 milhão de downloads" e conteúdo com data para expirar.
- Alt text de até 140 caracteres.

**Screenshots**
- JPEG ou PNG de 24 bits sem alfa. Lado mínimo de 320 px e máximo de 3840 px; o lado maior pode ter **no máximo 2× o menor**.
- Até **8 por tipo de aparelho**, com mínimo de 2. Para ser elegível a recomendações, o app precisa de **pelo menos 4 screenshots com no mínimo 1080 × 1920** em retrato. Por isso usamos 1080 × 1920.
- Precisam mostrar a **experiência real do app**.
- **Legenda só se necessário, ocupando no máximo 20% da imagem.**
- Proibido: alegações de desempenho, ranking, prêmio ou depoimento ("melhor", "nº 1"), preço ou desconto, **chamada para ação** ("baixe agora"), marcas de terceiros sem permissão e selos de loja.
- Barra de status limpa: sem notificações nem operadora; bateria, Wi-Fi e sinal cheios.
- Se houver texto na imagem, **fazer uma versão por idioma**.

**Textos**
- Título: até **30** caracteres, sem emoji, sem caixa alta (a menos que a marca seja assim), sem "grátis" ou "sem anúncios".
- Descrição curta: até **80** caracteres, sem emoji, caracteres especiais, quebra de linha ou chamada para ação. Deve dizer o propósito e o principal benefício.
- Descrição longa: até **4000** caracteres, linguagem natural sem lista de palavras-chave, sem repetir a descrição curta, sem comparar com outros apps e sem depoimento anônimo.

## 2. O que os apps de topo fazem

| App | Observação |
|---|---|
| **Duolingo** | As 3 primeiras imagens vendem **benefícios** e não telas. A descrição abre com 3 frases curtas, depois seções com título e bullets ("Why Duolingo?"). Usa emoji só como marcador de seção. |
| **Letterboxd** | Descrição curta e direta. Abre com uma frase de proposta e uma lista de ~11 recursos em bullets. |
| **IMDb** | Título com palavra-chave ("IMDb: Movies & TV Shows") e descrição curta em 2 parágrafos de prosa (recursos + idiomas). |
| **Headway** | A primeira imagem tem cor forte e explica o produto numa frase. A análise do Gummicube critica as telas seguintes por serem repetitivas e por terem subtítulos finos e pequenos. |
| **Trainline, Snapchat** (citados pelo AppTweak) | Cor de marca forte e legenda com alto contraste em todas as telas. |
| **Netflix, Spotify, Blinkist e outros apps de mídia** | Fundo escuro, arte de conteúdo em destaque, legenda curta em fonte grossa no topo e aparelho ou tela cheia logo abaixo. A série é consistente (mesma grade e paleta). *Isso é observação geral do padrão do segmento, não de um artigo específico.* |

### Conclusões aplicadas no v2

1. **A primeira imagem vende o principal**: "130 anos de cinema em 8 eras", com a Home e uma era. As 3 primeiras imagens aparecem na busca e precisam se sustentar sozinhas, então seguimos com Capítulos e Prêmios.
2. **Um benefício por imagem**, com legenda de **2 a 6 palavras** em fonte grossa (Oswald Bold, a fonte do app) e um subtítulo curto opcional em Proxima Nova grande o bastante para miniatura (≥ 40 px em 1080).
3. **Legenda sempre no topo** e dentro de ~20% da área. A interface fica logo abaixo e nunca tem texto por cima.
4. **Fundo escuro cinematográfico** (preto quente com feixe de projetor, grão de película e vinheta) e **dourado** como cor de destaque, que conversa com a tela de Prêmios. A mesma grade se repete nas 8 imagens.
5. **Mockup de celular genérico em CSS**, sem marca de fabricante, inclinado em composições de 1, 2 ou 3 aparelhos para dar ritmo sem perder consistência.
6. **Localização**: 3 versões de cada imagem (pt-BR, en-US e es-419), com espaço para prints do app em cada idioma (`screens-raw/<idioma>/`).
7. **Feature graphic sem aparelho e sem o ícone**: título centralizado e uma faixa de película com o ano de início de cada uma das 8 eras. Não tem texto pequeno no centro exato, onde a Play coloca o botão de play quando há vídeo.
8. **Descrição** no padrão Duolingo/Letterboxd: parágrafo de abertura com a proposta, seções em CAIXA ALTA, bullets "•", sem emoji e com as palavras-chave naturais (história do cinema, filmes, Oscar, linha do tempo, 1001 filmes, diretores, glossário). A atribuição do TMDB fica no fim.
9. **Marcas de terceiros**: os nomes das premiações aparecem **só na descrição longa**, de forma descritiva e com aviso de que não há vínculo oficial. **Nas legendas das imagens não usamos nomes de marcas** ("Os grandes prêmios" em vez de "Oscar"). Atenção aos prints: logos e estatuetas que aparecem dentro do app (estatueta do Oscar, logotipo Walt Disney no glossário etc.) podem ser questionados pela regra de marcas. Ao capturar os prints novos, prefira telas em que isso não seja o elemento principal.

## Fontes

- Google Play Console Help: Add preview assets to showcase your app — https://support.google.com/googleplay/android-developer/answer/9866151
- Google Play Console Help: Best practices for your store listing — https://support.google.com/googleplay/android-developer/answer/13393723
- AppTweak: How to optimize your app screenshots — https://apptweak.com/aso-blog/how-to-optimize-your-app-screenshots
- Sonar: Google Play Feature Graphic, specs e dicas — https://trysonar.app/blog/google-play-feature-graphic
- Sonar: legendas de screenshots — https://trysonar.app/blog/app-store-screenshot-captions
- Gummicube: Headway ASO analysis — https://www.gummicube.com/blog/headway-daily-micro-learnings-aso-analysis
- AppRadar: Android app screenshot sizes and guidelines — https://appradar.com/blog/android-app-screenshot-sizes-and-guidelines-for-google-play
- SD Times: Google updates guidelines for Play Store listings — https://sdtimes.com/mobile/google-updates-guidelines-for-creating-and-managing-play-store-app-listings/
- Página da Letterboxd na Play — https://play.google.com/store/apps/details?id=com.letterboxd.letterboxd
- Página do Duolingo na Play — https://play.google.com/store/apps/details?id=com.duolingo
- Página do IMDb na Play — https://play.google.com/store/apps/details?id=com.imdb.mobile
