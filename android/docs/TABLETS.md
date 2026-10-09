# Tablets e telas grandes

O app foi pensado para celular. Esta adaptação deixa a experiência boa em tablets, dobráveis
abertos e janelas redimensionáveis (multi-janela, modo desktop). A referência é o nível 2
("large screen optimized") das diretrizes de qualidade para telas grandes do Android.

**Regra principal: o celular não muda.** Tudo vem de recursos qualificados. No celular
os valores são neutros (`0dp` = sem limite, 1 coluna), então cada adaptação vira um no-op.

## Breakpoints (window size classes do Material 3)

| Classe   | Largura da janela | Exemplos                          | Recursos                          |
|----------|-------------------|-----------------------------------|-----------------------------------|
| Compact  | < 600dp           | celular (retrato e paisagem)      | `values/` (valores neutros)        |
| Medium   | 600–839dp         | tablet em retrato, dobrável aberto | `values-sw600dp/`                 |
| Expanded | ≥ 840dp           | tablet em paisagem, janela larga  | `values-sw600dp-w840dp/`          |

O "portão" é `sw600dp` (menor largura do aparelho/janela), e não só `w600dp`. Assim um celular
em paisagem (largura de ~800dp, mas sw < 600dp) continua idêntico. Dentro de `sw600dp`, a
largura atual (`w840dp`) separa medium de expanded e acompanha rotação e redimensionamento.

Todos os valores ficam em `res/values*/dimens_large_screen.xml` (prefixo `ls_`):

| Recurso                       | Compact | Medium | Expanded | Uso                                             |
|-------------------------------|---------|--------|----------|-------------------------------------------------|
| `ls_reading_max_width`        | 0       | 680dp  | 720dp    | coluna de texto dos capítulos                   |
| `ls_media_max_width`          | 0       | 840dp  | 840dp    | imagens, vídeos, GIFs, slides e carrosséis do capítulo |
| `ls_list_max_width`           | 0       | 760dp  | 840dp    | listas de uma coluna (glossário, referências, linha do tempo, eras, sumário) |
| `ls_details_max_width`        | 0       | 840dp  | 960dp    | detalhes de filme/pessoa, abas de prêmios       |
| `ls_form_max_width`           | 0       | 600dp  | 600dp    | apoio, onboarding, sobre, configurações         |
| `ls_mini_player_max_width`    | 0       | 520dp  | 520dp    | mini-player da narração                         |
| `ls_sheet_max_width`          | 0       | 640dp  | 640dp    | player expandido (bottom sheet)                 |
| `ls_card_columns`             | 1       | 2      | 3        | grades de cartões (prêmios, diretores, 1001 filmes) |
| `ls_home_destination_columns` | 2       | 4      | 4        | grade "Explorar" da Home                        |
| `ls_list_side_padding`        | 0       | 8dp    | 16dp     | somado aos 16dp dos itens: margens de 24dp / 32dp |
| `ls_media_height_scale`       | 1.0     | 1.5    | 1.5      | alturas fixas (dp) vindas do JSON do conteúdo   |

Margens da tela: 16dp no celular, 24dp em medium e 32dp em expanded (`home_screen_padding` e
`ls_list_side_padding`). As alturas de imagem, vídeo, GIF, slide e cabeçalho dos capítulos, assim
como as do topo das telas de filme e pessoa, crescem em `values-sw600dp` (são dimens do módulo
helpers, sobrescritas pelo app).

## Ferramentas

- `presentation/configs/LargeScreen.kt`
  - `View.limitContentWidth(@DimenRes)`: centraliza o conteúdo de um container de tela cheia
    (RecyclerView, NestedScrollView, Shimmer) com padding lateral simétrico. Como a view continua
    com a largura toda, dá para rolar também pelas laterais. Recalcula ao redimensionar e resiste a
    padding de insets.
  - `ReadingWidthDecoration`: ItemDecoration da leitura. Parágrafos ficam em uma coluna e as mídias
    em outra, mais larga, sempre centralizadas.
  - `BalancedGridSpanLookup`: grade sem buracos para listas com itens de largura total no meio
    (citações, anúncio). As sobras de uma sequência dividem a última linha.
  - `ImageSize.forScreen(context)`: em `sw600dp` pede ao TMDB um tamanho acima
    (w500 → w780, w780 → w1280…).
- `presentation/views/MaxWidthLayouts.kt`: `MaxWidthFrameLayout` e `MaxWidthLinearLayout`, com o
  atributo `app:ls_maxWidth`. Funcionam como `max-width` do CSS. O pai é quem centraliza, com
  `layout_gravity="center_horizontal"`.
- `ConstraintLayout`: `app:layout_constraintWidth_max="@dimen/ls_…"` (0 = sem limite).
- `TextView` com `wrap_content`: `android:maxWidth="@dimen/ls_text_max_width"`. No celular o valor
  é 4000dp, porque `maxWidth` igual a 0 esconderia o texto.

## O que foi adaptado

1. **Leitura dos capítulos** (`HistoryPageFragment`, `fragment_history_page.xml`)
   - Parágrafos, citações, ensaios, anúncio e cartão de apoio ficam numa coluna de 680–720dp.
   - Imagens, vídeos, GIFs, slides e carrosséis de filmes/pessoas vão até 840dp.
   - A lista continua rolando na tela inteira.
   - Título e descrição do cabeçalho têm largura máxima. A imagem do cabeçalho e as mídias ficam
     mais altas (dimens e `ls_media_height_scale` para alturas que vêm do JSON).
   - O esqueleto de carregamento segue a mesma coluna.
   - Mini-player: largura máxima de 520dp, centralizado no espaço livre do rodapé.
   - Player expandido (`AudioPlayerSheet`): `behavior.maxWidth` = 640dp, centralizado.
2. **Home**
   - Margens de 24dp em medium e 32dp em expanded.
   - Carrossel de eras mais alto, com itens maiores (o carrossel M3 mostra mais itens sozinho).
   - "Explorar" em 4 colunas.
   - O texto do destaque não atravessa a imagem (560dp).
   - Os cartões de citação e de links têm largura máxima.
   - A altura do destaque usa a altura da janela, não a do display.
3. **Listas e grades**
   - Prêmios, diretores e 1001 filmes (`MainTopicsActivity`): grade de 2 ou 3 colunas, com
     citações e anúncio ocupando a linha toda.
   - Eras: continuam em coluna única centralizada, porque é uma sequência editorial com cartões de
     formatos diferentes.
   - 1001 filmes (`MilMoviesPresentationActivity`): cada página tem largura de pôster,
     proporcional à altura da janela, e os vizinhos aparecem dos lados. Assim o pôster não vira
     uma faixa em paisagem.
   - Sumário (`PresentationActivity`), glossário, referências e linha do tempo: coluna
     centralizada. O índice A–Z e a marca d'água do ano continuam nas bordas da tela.
   - Prêmios: o destaque e as abas "Sobre" e "Indicados" usam a coluna de detalhes. As listas
     horizontais mostram mais itens naturalmente.
   - Filme e pessoa: o cabeçalho (título, notas, botões) e os blocos de informação usam a coluna de
     detalhes, e o topo fica mais alto. O backdrop do filme e os pôsteres de 1001 filmes usam
     imagens TMDB maiores.
4. **Apoio, onboarding, sobre, configurações**
   - Conteúdo numa coluna de 600dp.
   - Os botões fixos (apoio) e a barra "pontos + Próximo" (onboarding) não esticam até as bordas.
5. **Orientação e recriação**
   - Nenhuma Activity trava orientação (`screenOrientation`) e nenhuma desativa
     `resizeableActivity`. Isso é obrigatório: no Android 16 com targetSdk 36, restrições de
     orientação e de redimensionamento são ignoradas em telas ≥ 600dp.
   - Linha do tempo: a página aberta agora sobrevive à recriação (rotação, redimensionamento). Antes
     ela voltava para a página do intent, porque o adapter é definido depois do carregamento.
   - Capítulos: a posição para compartilhar começa certa. O ViewPager2 restaura a página sozinho.
   - O player de áudio vive no `AudioPlaybackService`. O sheet é restaurado pelo FragmentManager e
     se fecha sozinho se não houver fila.

## Como testar

- Emuladores "Medium Tablet" e "Pixel Fold"/"Pixel Tablet", em retrato e paisagem.
- Multi-janela e janela livre (desktop): redimensionar atravessando 600dp e 840dp.
- Telas para conferir:
  - capítulo com áudio (mini-player e player expandido);
  - Home;
  - prêmios (ano aberto e rotação);
  - 1001 filmes;
  - linha do tempo (rotação no meio);
  - onboarding.
- Celular (compact): comparar capturas antes e depois. Não deve haver diferença.

## Próximos passos possíveis

- Painel duplo em expanded (lista e detalhe), por exemplo sumário à esquerda e capítulo à direita
  (SlidingPaneLayout ou `androidx.window` + `WindowSizeClass`).
- Navigation rail na Home em expanded.
- Teclado e mouse (nível 1 das diretrizes): atalhos (setas para trocar de capítulo, espaço para
  play/pause), foco visível, hover nos cartões e menu de contexto com clique direito.

---

## Para o iOS (iPhone/iPad)

Equivalentes recomendados para a futura versão iOS:

- **Size classes.** Use `horizontalSizeClass` (`.compact` / `.regular`) no lugar de breakpoints
  fixos.
  - iPhone em retrato é compact.
  - iPad e iPhone Max/Plus em paisagem são regular.
  - No iPad em Split View/Slide Over a classe muda com a janela. Reaja ao ambiente
    (`@Environment(\.horizontalSizeClass)`), nunca ao modelo do aparelho.
- **Largura de leitura.** Use 680–720pt para texto, centralizado, e até ~840pt para imagens e
  vídeos.
  - Em UIKit, use `readableContentGuide`.
  - Em SwiftUI, use `.frame(maxWidth: 700)` dentro de um container de largura total, para manter a
    rolagem pelas laterais.
  - Telas de formulário (apoio, onboarding, sobre) devem ficar com ~600pt.
- **Multitarefa no iPad.** Suporte Split View, Slide Over e Stage Manager: janela redimensionável,
  sem `UIRequiresFullScreen` e todas as orientações em `UISupportedInterfaceOrientations~ipad`.
  - Teste janelas estreitas (compact dentro do iPad) e muito largas (Stage Manager em monitor
    externo).
  - Guarde o estado com `@SceneStorage` / state restoration, para não perder o capítulo ao
    redimensionar ou trocar de cena.
- **Navegação.** No iPad use `NavigationSplitView`:
  - sidebar com eras e áreas (1001 filmes, prêmios, linha do tempo, diretores);
  - content com o sumário da era;
  - detail com o capítulo.
  - Em compact ele vira uma pilha automaticamente (equivale ao fluxo atual do Android).
- **Grades.** Use `LazyVGrid` com `GridItem(.adaptive(minimum: 280))` para prêmios e diretores, que
  dá mais colunas conforme a largura. Carrosséis horizontais podem mostrar mais itens. Use
  `containerRelativeFrame` para dimensionar por fração da largura.
- **Dynamic Type.**
  - Use estilos de texto (`.body`, `.title`…) ou `UIFontMetrics` para as fontes personalizadas
    (Oswald, Proxima Nova).
  - Teste até os tamanhos de acessibilidade. Com tipos muito grandes, prefira uma coluna e deixe os
    cabeçalhos quebrarem em várias linhas.
  - A largura de leitura em pontos continua válida: o texto fica maior, mas a coluna não estica.
- **Player de áudio.**
  - Mini-player: largura limitada no iPad (`safeAreaInset(edge: .bottom)` com `maxWidth` ~520pt).
  - Player completo: `.sheet` com `.presentationDetents`. No iPad ele vira um form sheet
    centralizado, sem ocupar a tela toda.
  - Integre com `MPNowPlayingInfoCenter` e `MPRemoteCommandCenter`.
- **Imagens.** Peça tamanhos do TMDB por largura em pixels (pontos × `displayScale`). Use
  w780/w1280 para backdrops no iPad.
- **Teclado e ponteiro.**
  - `.keyboardShortcut` para próximo/anterior capítulo e play/pause.
  - `.hoverEffect` nos cartões.
  - Menus de contexto (`.contextMenu`).
