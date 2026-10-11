# Cinema History para iOS (iPhone e iPad) — plano

Método: **(1)** levantar o que o app Android faz hoje (casos de uso e regras), **(2)** desenhar cada tela no jeito
do iOS, **(3)** manter fidelidade só onde ela importa (conteúdo, identidade visual, regras de negócio), e
**(4)** só então implementar, reaproveitando o máximo do que já existe.

## 0. Decisões tomadas (10/10/2026)

| # | Decisão |
|---|---|
| 1 | iOS mínimo **17** |
| 2 | Ponte Swift ↔ Kotlin com a **exportação padrão do Kotlin/Native** (sem SKIE), Kotlin **2.4.21** igual ao Android; regras e contrato da ponte abaixo |
| 3 | Imagens com **Kingfisher** |
| 4 | Áudio no iOS em **AAC (.m4a)**, cópia gerada a partir dos arquivos atuais |
| 5 | Android **migra para o núcleo `:shared` depois** do lançamento iOS |
| 6 | Nomes na App Store iguais ao Android: **História do Cinema / History of Cinema / Historia del Cine** |

Arquitetura: núcleo **Kotlin Multiplatform** (`shared/`) + UI **100% SwiftUI nativa**. A pasta `ios/` antiga
(iOS 15, ~300 arquivos) é removida na fase F0.

---

## 1. Inventário: o que o app Android faz hoje

### 1.1 Áreas e casos de uso

| Área | Casos de uso | Regras que precisam ser iguais |
|---|---|---|
| **Abertura** | Splash; onboarding na 1ª abertura (3 páginas + página "Quer ajudar?" só com oferta disponível): eras, escolha de idioma e tema | Onboarding só em instalação nova; página de apoio só BR + pt |
| **Home** | Hero "Continue lendo" (última era lida) com Continuar / Todas as eras; carrossel das 8 eras; citação da era em destaque; "Explore mais" (1001 Filmes, Premiações, Linha do tempo, Mestres das Telas/Diretores); "Mais" (Glossário, Referências, Configurações, Sobre); chip de idioma PT/EN/ES e botão de tema na barra; menu ⋮ (Apoie, Referências, Glossário, Configurações, Sobre) | "Continue lendo" = última era aberta; idioma do conteúdo troca na hora |
| **História do Cinema** | Lista das 8 eras (cor e imagem por era) → **Sumário** da era (imagem, citação, descrição, "Iniciar", lista de capítulos) → **Leitor** de capítulos | Ordem e conteúdo idênticos; cor da era nos destaques |
| **Leitor de capítulo** | Páginas em sequência (Anterior/Próximo, indicador de páginas, card "Sumário"); cabeçalho cinematográfico preto (era, título, descrição, "Ouvir · N min", imagem); blocos: **texto** (HTML simples, links para filme/pessoa/tela), **imagem**, **GIF**, **vídeo** (YouTube), **slide** (carrossel), **citação**, **bloco especial**, **ensaio** (canal + vídeo), **lista de filmes**, **lista de pessoas**, **recomendações / Vale a conferida**, **indicados a prêmios**, **link para tela**, **post do X**, **áudio**; compartilhar capítulo; Glossário/Referências; anúncio nativo no meio do texto; intersticial entre capítulos; card de apoio no fim; marca capítulo como lido | Regra de ritmo do conteúdo (já está no JSON); anúncio entre dois parágrafos, longe dos botões; sem anúncios para apoiador |
| **Narração em áudio** | Botão "Ouvir"; mini-player (capítulo · faixa, play/pausa, −15 s, expandir); player completo (faixas, avançar/voltar, velocidade, timer de sono, "ouvir a era inteira", baixar para ouvir offline); **acompanhar o texto** (rolagem e destaque do parágrafo lido, chip "Voltar ao trecho"); "Ouvir a partir daqui" (toque longo no parágrafo); continua com tela apagada; controles na notificação/tela de bloqueio; retoma de onde parou | Só pt por enquanto; sem apoio: só a faixa "Abertura" toca, o resto abre a oferta; marcas `source_index` do manifest |
| **1001 Filmes** | Décadas → carrossel de pôsteres por década (título, idioma, gênero) → **Detalhes do filme** | — |
| **Detalhes do filme** | Cabeçalho (backdrop, pôster, título, ano, nota); sinopse; elenco; equipe; **Você sabia?**; **citação**; **críticas por idioma** (pt/en/es, link para a original); filmes do diretor; coleção; produção; mídias (vídeos, imagens); **Onde assistir** (streaming do país + YouTube para filmes livres); bloco especial; tópicos relacionados; compartilhar; anúncio nativo | Dados do TMDB no idioma do conteúdo; provedores por país |
| **Premiações** | Lista de prêmios (Oscar, Globo de Ouro, BAFTA, Critics Choice, SAG, Spirit, Cannes) → tela do prêmio: aba **Filmes e vencedores** (linha do tempo de anos, estilo Netflix, categorias com indicados e vencedor, vídeo/imagem a cada 2 categorias) e aba **Sobre**; compartilhar | Atualizável pela Cloudflare sem nova versão |
| **Diretores ("Mestres das Telas")** | Lista com filtros por época e região, carrossel "Em alta", seções por época → página especial do diretor (perfil, biografia em linha do tempo com mídias, filmografia, prêmios, citação, vídeos) | 45 diretores, ranking "Em alta" |
| **Pessoa (TMDB)** | Biografia, filmografia, mídias, compartilhar | — |
| **Linha do tempo** | Páginas por período com animação ao rolar; compartilhar posição | — |
| **Glossário / Referências** | Termos e definições; referências por categoria (livros, sites, diretores, curiosidades…) com links | — |
| **Configurações** | Idioma do conteúdo, tema (sistema/claro/escuro), Apoie o app, Referências | — |
| **Apoio (compra única)** | Oferta com 3 valores (R$ 19,90 com lançamento R$ 14,90 / 29,90 / 49,90), benefícios (sem anúncios + áudio), aviso de IA na narração, restaurar compra, pagamento pendente (Pix/boleto), agradecimento para apoiador | Só BR + conteúdo pt; benefício igual nos 3 valores; "pagamento único, sem assinatura" |
| **Sobre** | Projeto, autor, créditos, agradecimento aos apoiadores, painel de debug escondido (7 toques na versão) | — |
| **Transversal** | Modo escuro; tablets (coluna de leitura, grades); conteúdo remoto (prêmios) a cada 6 h; atalhos de app (História, 1001, Linha do tempo, Prêmios, Diretores); links compartilháveis (`thshoc.link/?screen=…`); Crashlytics/Analytics; AdMob + consentimento (UMP) | — |

### 1.2 Pontos a resolver junto (valem para os dois apps)

- **Links compartilháveis**: hoje usam `thshoc.link`. No iOS, abrir o app por link exige **Universal Links** com
  um arquivo `apple-app-site-association` no domínio. Sem domínio próprio, a saída é usar o site
  (`website-cb5.pages.dev/cinema-history/...`) como página de destino com botões para as lojas. **[decidir na F4]**
- **Busca**: o Android não tem; no iOS é esperado (`.searchable`). Fica como melhoria futura para os dois.

---

## 2. Princípios de design para o iOS

**Manter fiel (identidade e conteúdo):**
- Conteúdo, ordem, textos e mídias exatamente iguais (mesmo JSON).
- Identidade visual: cabeçalhos cinematográficos em preto com imagem; **cor de cada era** nas pílulas e destaques;
  pôsteres e retratos grandes; citações com aspas grandes; tipografia **Proxima Nova** nos títulos (mesma família
  do Android) — corpo do texto em **SF Pro / New York** para leitura longa, respeitando Dynamic Type. **[validar]**
- Ritmo de leitura e regras de anúncio/apoio.
- Comportamento do áudio (acompanhar o texto, prévia da Abertura, retomar).

**Fazer do jeito do iOS (não copiar o Android):**
- Navegação: **TabView** no iPhone e **barra lateral** (`NavigationSplitView`) no iPad, em vez da Home "hub" com menu ⋮.
- Títulos grandes que encolhem ao rolar, voltar com gesto de borda, `sheet` com *detents* para player e filtros.
- **Menus de contexto** (toque longo) em capítulos, filmes e diretores: Compartilhar, Ouvir a partir daqui, Abrir.
- Controles nativos: `Picker` segmentado (abas do prêmio, filtros), `Toggle`, `Menu` na toolbar (idioma/aparência),
  `ShareLink`, SF Symbols.
- Configurações no estilo **Ajustes** (`Form` em grupos).
- Idioma: o iOS já tem "Idioma por app" nos Ajustes do sistema; o app mostra o seletor de **conteúdo** e um atalho
  para os Ajustes do sistema (mesmo comportamento final do Android: conteúdo e interface no idioma escolhido).
- Áudio: mini-player flutuante acima da Tab Bar (padrão Música/Podcasts), player completo em `sheet`, Now Playing
  na tela de bloqueio, Central de Controle, AirPods e CarPlay.
- Acessibilidade: Dynamic Type até os tamanhos de acessibilidade, VoiceOver em todos os blocos, Reduzir movimento
  (desliga animações da linha do tempo e paralaxe), contraste.
- Atalhos do Android → **Quick Actions** (toque longo no ícone) + **App Intents** (Siri/Spotlight: "Continuar lendo",
  "Ouvir capítulo").

---

## 3. Estrutura de navegação

### iPhone — Tab Bar com 4 abas
| Aba | Conteúdo |
|---|---|
| **Início** | Hero "Continue lendo", carrossel das eras, citação, atalhos para as seções |
| **História** | As 8 eras → Sumário → Leitor |
| **Explorar** | 1001 Filmes, Premiações, Mestres das Telas, Linha do tempo, Glossário, Referências |
| **Ajustes** | Idioma, Aparência, Apoie o app (quando disponível), Sobre, Privacidade e Termos |

O mini-player aparece acima da Tab Bar em qualquer aba quando há narração na fila.

### iPad — barra lateral (3 colunas onde fizer sentido)
- Coluna 1: Início, História, 1001 Filmes, Premiações, Mestres das Telas, Linha do tempo, Glossário, Referências, Ajustes.
- História: **eras | sumário | leitor** (3 colunas; o leitor ocupa a coluna de detalhe com coluna de leitura de ~680 pt).
- Premiações/1001/Diretores: lista na coluna do meio, detalhe à direita.
- Suporte a Split View, Stage Manager, teclado (⌘[ ⌘] para capítulo anterior/próximo, espaço para play/pausa).

---

## 4. Desenho das telas

Cada tela: **layout iOS** · **componentes nativos** · **fidelidade** · **diferença em relação ao Android**.
A F1.5 gera prévias reais (SwiftUI, prints do simulador iPhone e iPad, claro e escuro) para aprovação antes de
implementar a lógica completa.

### 4.1 Início
- **Layout**: `ScrollView` com título grande "História do Cinema"; hero em tela cheia até o topo (por trás da barra
  de status) com era em destaque, "Continue lendo", botões **Continuar** (preenchido) e **Todas as eras**;
  carrossel horizontal das eras com *paging* e cartões 3:4; cartão de citação; grade "Explore" (2 colunas iPhone,
  4 iPad).
- **Nativo**: `ScrollView(.horizontal)` + `scrollTargetBehavior(.viewAligned)`, `ContainerRelativeFrame`,
  toolbar com `Menu` (Idioma: Português/English/Español · Aparência: Sistema/Claro/Escuro).
- **Fiel**: hero, carrossel e citação iguais; cores das eras.
- **Muda**: sem menu ⋮ (vai para a aba Ajustes); chip de idioma vira `Menu` na toolbar.

### 4.2 História → Sumário
- **Eras**: lista com cartões grandes (imagem + "Parte 0X" na cor da era + período + resumo).
- **Sumário**: cabeçalho com imagem em paralaxe e citação; título e descrição da era; botão **Iniciar**
  (ou **Continuar** se já leu); lista de capítulos com indicador de lido (✓) e duração do áudio quando houver.
- **Nativo**: `List` com seções, *swipe actions* (marcar como lido), menu de contexto (Ouvir, Compartilhar).

### 4.3 Leitor de capítulo (tela mais importante)
- **Cabeçalho**: fundo preto, pílula da era na cor da era, título em caixa alta, descrição, botão **Ouvir · N min**,
  imagem do capítulo com degradê — igual ao Android. Encolhe para a barra de navegação ao rolar.
- **Corpo**: `ScrollView` + `LazyVStack`; cada tipo de bloco vira uma View:
  texto → `Text(AttributedString)` com links (filme/pessoa/tela abrem em *push*; links externos em `SFSafariViewController`);
  imagem → Kingfisher + legenda/fonte; GIF → `KFAnimatedImage`; vídeo → miniatura + player do YouTube em `sheet`
  (WKWebView, sem autoplay); slide → `TabView(.page)`; citação → aspas grandes; ensaio, bloco especial, listas de
  filmes/pessoas, recomendações e indicados → componentes próprios com rolagem horizontal; post do X → cartão com link.
- **Navegação entre capítulos**: barra inferior com **Sumário** (miniatura) · indicador · **Próximo**; deslizar
  horizontalmente também troca (com `TabView(.page)` por capítulo). Gesto de borda volta para o sumário.
- **Toolbar**: Aparência, Idioma (`Menu`), Compartilhar, ⋯ (Glossário, Referências).
- **Leitura**: Dynamic Type, coluna de ~680 pt no iPad, "Reduzir movimento" respeitado.
- **Anúncios**: nativo entre dois parágrafos (mesma regra do `shared`), intersticial entre capítulos com o mesmo
  limite de frequência; nada para apoiador.
- **Fim do capítulo**: card de apoio (quando disponível) e "Próximo capítulo".

### 4.4 Narração em áudio
- **Mini-player** flutuante acima da Tab Bar/barra inferior: capa, "Capítulo N · Título" + faixa, play/pausa, −15 s;
  tocar abre o player.
- **Player** em `sheet` (*detent* grande): capa, título, barra de progresso com tempo, −15/+30, anterior/próxima
  faixa, velocidade (0,8×–2×), timer de sono (15/30/45/60 min, fim da faixa), "Ouvir a era inteira" (`Toggle`),
  baixar para ouvir offline (com progresso), lista de faixas.
- **Acompanhar o texto**: parágrafo lido com fundo suave; rolagem automática; botão flutuante "Voltar ao trecho"
  quando o usuário rola para longe; menu de contexto no parágrafo → "Ouvir a partir daqui".
- **Sistema**: `AVPlayer` + Background Audio + `MPNowPlayingInfoCenter` + `MPRemoteCommandCenter`; interrupções
  (ligação, outro app) e rota (fone desconectado pausa).
- **Fiel**: prévia só da Abertura sem apoio; retomar posição; mesmos manifests (campo novo `file_m4a`).

### 4.5 1001 Filmes → Detalhes do filme
- **Décadas**: lista com cartões; **década**: carrossel de pôsteres com *paging* (iPhone) / grade adaptativa (iPad).
- **Detalhes**: cabeçalho com backdrop esmaecido + pôster + título, ano, duração, nota; seções em ordem fixa:
  Sinopse · Onde assistir · Você sabia? · Citação · Críticas (`Picker` segmentado Português/Inglês/Español,
  começando no idioma da interface; cada crítica abre a original) · Elenco e equipe (rolagem horizontal) · Do mesmo
  diretor · Coleção · Mídias · Produção.
- **Onde assistir**: provedores do país (logo + tipo) e **YouTube (grátis, filme completo)** quando houver; abre o
  app do serviço ou o navegador.
- **Toolbar**: Compartilhar.

### 4.6 Premiações
- **Lista**: cartões dos 7 prêmios com logo.
- **Tela do prêmio**: cabeçalho com logo no círculo; `Picker` segmentado **Filmes e vencedores | Sobre**.
  Em "Filmes e vencedores": seletor de ano horizontal (anos em chips, ano atual centralizado), destaque do vencedor
  principal com backdrop, categorias com indicados (vencedor marcado com 🏆 / cor dourada), vídeo ou imagem a cada
  2 categorias. Animação de entrada sutil, desligada com "Reduzir movimento".

### 4.7 Mestres das Telas (diretores)
- **Lista**: título grande, filtros de época e região em `Menu`/chips no topo (fixos ao rolar), carrossel
  "Em alta" com posição (#1…), seções por época com grade de retratos 3:4 (2 colunas iPhone, 3–4 iPad).
- **Página do diretor**: cabeçalho com retrato, nome, anos, país, frase; seções: Biografia (linha do tempo com
  mídias), Filmografia, Prêmios, Citação, Vídeos.

### 4.8 Linha do tempo
- Páginas por período (`TabView(.page)` ou rolagem vertical com âncoras por ano); animação ao rolar
  (`scrollTransition`), respeitando "Reduzir movimento"; compartilhar a posição.

### 4.9 Glossário e Referências
- Glossário: `List` com seções alfabéticas, índice lateral e busca local (`.searchable`).
- Referências: `List` agrupada por categoria; links em `SFSafariViewController`.

### 4.10 Ajustes, Sobre, Apoio, Onboarding
- **Ajustes** (`Form`): Idioma do conteúdo · Idioma do app (abre os Ajustes do sistema) · Aparência ·
  Apoie o História do Cinema (ou "Você apoia ♥") · Restaurar compras · Sobre · Privacidade · Termos.
- **Apoio** (`sheet`): mesma mensagem do Android ("Cultura é gratuita"), benefícios, aviso da narração por IA,
  3 valores (preços localizados pelo StoreKit, oferta de lançamento como *introductory/promotional offer*
  **[verificar no App Store Connect]**), "Pagamento único, sem assinatura", Restaurar compras, links de
  Termos/Privacidade (exigência da Apple).
- **Onboarding** (`fullScreenCover`): mesmas páginas; idioma e aparência; página de apoio só BR + pt.
- **Sobre**: igual; painel de debug só em build de desenvolvimento.

---

## 5. Reaproveitamento técnico

| Ativo | Reaproveita? | Como |
|---|---|---|
| Conteúdo JSON (`android/app/src/main/assets/local/{pt,en,es}`, ~65 MB) | **100%** | O Xcode embute a mesma pasta (referência de pasta). Uma fonte só. |
| Pipelines (`content-src/`, `content-i18n/`) | **100%** | Sem mudança. |
| Conteúdo remoto (Cloudflare `content/manifest.json`) | **100%** | Lógica de sincronização vai para o `shared`. |
| Áudio (R2 + Worker) | **100%** + cópia AAC | `generate.py`/`upload.py` ganham a saída `.m4a`; manifest com `file_m4a`. |
| Regras (idioma, oferta, anúncios, TMDB, prêmios, marcas do áudio) | **Reescritas uma vez no `shared`** | Hoje presas a RxJava/Gson/SharedPreferences. |
| Modelos (`entities/`, 115 classes Gson) | **Portados** | `@Serializable` (kotlinx.serialization). |
| Camada de dados Android (Retrofit + FakeInterceptor, Dagger) | **Não** | Substituída no `shared` por repositório de arquivos + Ktor. |
| Textos da interface (strings pt/en/es) | **Sim** | Script `strings*.xml` → `Localizable.xcstrings`. |

```
cinema-history/
├─ shared/                 ← Kotlin Multiplatform
│  ├─ commonMain/ model · content · remote · tmdb · audio · support · ads · i18n
│  ├─ androidMain/ iosMain/  (arquivos, preferências, HTTP)
│  └─ commonTest/
├─ android/               (migra para :shared depois do iOS)
└─ ios/CinemaHistory/     ← SwiftUI (iOS 18+), Kingfisher, GoogleMobileAds, Firebase
```
Bibliotecas KMP: kotlinx.serialization, kotlinx.coroutines, Ktor (OkHttp/Darwin), multiplatform-settings, okio.

## 6. Áudio no iOS (AAC)

O AVPlayer não toca Opus/OGG. Gerar **`.m4a` (AAC-LC 64 kbps mono ou HE-AAC 48 kbps)** a partir dos `.ogg` já
publicados (Opus → AAC, sem custo de TTS), com o mesmo hash no nome, e acrescentar `file_m4a` em cada faixa do
`manifest.json`. O Android segue com OGG. ~+0,35 GB no R2 (pt), dentro do grátis. Gerar em AAC direto do PCM nas
próximas gerações (en/es).

## 7. Fases

| Fase | Entrega | Como validar |
|---|---|---|
| **F0 — Base** | Remover `ios/` antigo; criar `shared/` e `ios/CinemaHistory`; CI em macOS (XCFramework, testes, build do simulador) | GitHub Actions macOS |
| **F1 — Núcleo** | Modelos, leitura do conteúdo por idioma, conteúdo remoto, regras, manifest de áudio + testes | Testes JVM + simulador |
| **F1.5 — Protótipo de telas** | Telas da §4 em SwiftUI com dados reais, sem lógica completa; prints iPhone e iPad, claro e escuro, pt/en/es | **Sua aprovação** antes de seguir |
| **F2 — Leitura** | Início, História, Sumário, Leitor completo, idioma e aparência | Prints + TestFlight interno |
| **F3 — Seções** | 1001 Filmes + Detalhes, Premiações, Diretores, Pessoa, Linha do tempo, Glossário, Referências | idem |
| **F4 — Áudio e monetização** | Cópia AAC; player; StoreKit 2; AdMob + UMP + ATT; Firebase; links e Quick Actions | Sandbox Apple / TestFlight |
| **F5 — Loja** | Ícones, prints (pt/en/es, iPhone e iPad), textos, rótulos de privacidade, revisão | App Store |
| **F6 — Android no `shared`** | Trocar a camada de dados do Android pelo núcleo | CI Android |

## 8. Ambiente e o que você precisa

- Swift e alvos iOS do KMP **só compilam em macOS**: compilação, testes e prints do simulador rodam no
  **GitHub Actions (macOS)** (grátis, repo público). Para rodar no seu iPhone/iPad e enviar à loja: **Xcode no seu Mac**.
- **Apple Developer Program** (US$ 99/ano).
- **App Store Connect**: app (bundle id sugerido `com.tiagohs.cinemahistory`), acordo de **Apps pagos** + dados
  bancários/fiscais, 3 produtos não consumíveis.
- **AdMob**: app iOS novo (o `app-ads.txt` atual já serve). **Firebase**: app iOS novo (`GoogleService-Info.plist`).
- Política de privacidade e termos: incluir iOS (ATT, StoreKit).


## Ponte Kotlin ↔ Swift (decisão de 11/10/2026)

A exportação padrão do Kotlin/Native foi escolhida no lugar do SKIE depois de um protótipo com 12 situações críticas
testadas no simulador (branch `spike/exportacao-padrao`). O SKIE só suporta Kotlin até a série 2.2; sem ele o núcleo
acompanha o Kotlin do Android e as bibliotecas mais novas.

Regras (verificadas por `RegrasDaPonteTest` no `shared` e por `PonteContratoTests` no iOS):

1. A fachada (`shared/.../bridge`) só expõe classes concretas: nada de `sealed` genérico, `Int?` ou coleções de números.
2. Trabalho demorado (rede, sincronização, download, TMDB) devolve `Cancelavel`; no Swift, `aguardar { … }` liga a alça
   ao cancelamento da `Task` (cancelar a `Task` sozinho **não** cancela a corrotina).
3. Estado observável é `observar…(aoMudar): Cancelavel` sobre `StateFlow`; no Swift, `observar { … }` vira `AsyncStream`.
4. A fachada não lança exceção; o que puder lançar leva `@Throws`.
5. Closures em Swift capturam `[weak self]`; observações são canceladas ao sair da tela (ciclo Swift↔Kotlin vaza).
6. Tipos com subtipos carregam um enum `tipo`; um teste em Swift garante que todo tipo tem tela.
7. `PonteContratoTests` roda a cada build: se uma atualização do Kotlin mudar um comportamento, o CI falha antes do app.
