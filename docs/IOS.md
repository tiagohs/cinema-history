# Cinema History para iOS (iPhone e iPad) — plano

> Rascunho para discussão. Decisões marcadas com **[decidir]** ainda precisam de OK.

## 1. Direção

Mesma linha do Metasfera: **núcleo compartilhado em Kotlin Multiplatform (KMP)** + **UI 100% nativa em SwiftUI**,
seguindo as convenções do iOS (navegação, tipografia, gestos, Dynamic Type), e não uma cópia da UI Android.
A pasta `ios/` atual (projeto nativo antigo, iOS 15, ~300 arquivos Swift, padrão de módulos por tela) é descartada
e recomeçada do zero.

## 2. O que dá para reaproveitar (e o que não dá)

| Ativo | Reaproveita? | Como |
|---|---|---|
| Conteúdo (`android/app/src/main/assets/local/{pt,en,es}`, ~65 MB de JSON) | **100%** | O app iOS embute a mesma pasta (referência de pasta no Xcode, sem cópia manual). Uma fonte só. |
| Pipelines (`content-src/`, `content-i18n/`, prêmios, diretores, filmes) | **100%** | Não mudam: geram o JSON que os dois apps leem. |
| Conteúdo remoto (Cloudflare: `content/manifest.json` + sha256) | **100%** | Mesmo servidor; a lógica de sincronização passa para o núcleo KMP. |
| Áudio (R2 + Worker, `manifest.json` por capítulo, marcas `source_index`) | **100%** | Mesmos arquivos Opus. **[atenção]** o AVPlayer não toca Opus/OGG: ver §6. |
| Regras de negócio (idioma do conteúdo, oferta de apoio só BR+pt, posição de anúncio no capítulo, tamanhos de imagem TMDB, índice de prêmios, busca de marcas do áudio) | **Sim, reescritas no KMP** | Hoje estão espalhadas em Kotlin Android (RxJava, Gson, SharedPreferences). Viram código comum testado. |
| Modelos (`entities/`, 115 classes com Gson) | **Sim, portados** | Viram `@Serializable` (kotlinx.serialization) no KMP. |
| Camada de dados Android (Retrofit + `FakeInterceptor` lendo assets, Dagger, RxJava) | **Não diretamente** | É presa ao Android/JVM. O KMP substitui por um repositório que lê arquivos + Ktor para TMDB/Cloudflare. |
| UI Android (XML/ViewBinding, adapters) | **Não** | O iOS terá UI própria em SwiftUI. O Android continua como está. |
| Textos da interface (strings pt/en/es) | **Sim** | Script converte `strings*.xml` → String Catalog (`Localizable.xcstrings`). |
| Guias (`docs/TABLETS.md` §iOS, `docs/APOIO.md`, `content-src/audio/LEIAME.md`) | **Sim** | Regras de layout e de produto valem para o iOS. |

**O Android migra para o núcleo KMP?** Não é obrigatório para o iOS sair. Sugestão: **depois** do iOS publicado,
trocar aos poucos a camada de dados do Android pelo módulo `shared` (tela por tela), para manter uma regra só. **[decidir]**

## 3. Arquitetura

```
cinema-history/
├─ shared/                  ← NOVO: Kotlin Multiplatform (Android + iOS)
│  ├─ commonMain/
│  │  ├─ model/             (capítulos, eras, filmes, pessoas, prêmios, diretores, timeline… @Serializable)
│  │  ├─ content/           (ContentRepository: lê o JSON local por idioma; fallback en; cache)
│  │  ├─ remote/            (RemoteContent: manifest + sha256, sincroniza a cada 6 h, só o idioma atual)
│  │  ├─ tmdb/              (cliente Ktor + regras de tamanho de imagem)
│  │  ├─ audio/             (AudioManifest, marcas → parágrafo, faixa liberada na prévia)
│  │  ├─ support/           (regras da oferta: país BR + conteúdo pt; benefícios)
│  │  └─ i18n/              (ContentLanguage: idiomas liberados, escolha, fallback)
│  ├─ androidMain/  iosMain/  (leitura de arquivos, preferências, engine HTTP)
│  └─ commonTest/           (testes rodando em JVM e no simulador iOS)
├─ android/                 (como hoje; opcionalmente passa a depender de :shared)
└─ ios/CinemaHistory/       ← NOVO projeto SwiftUI
```

- **Bibliotecas KMP**: kotlinx.serialization, kotlinx.coroutines (Flow), Ktor (OkHttp no Android, Darwin no iOS),
  multiplatform-settings (preferências), okio (arquivos). Sem banco de dados no início (tudo é JSON somente leitura).
- **Ponte Swift ↔ Kotlin**: **SKIE** (Touchlab) para `suspend` → `async/await`, `Flow` → `AsyncSequence` e
  `sealed class` → `enum` do Swift. Distribuição como XCFramework gerado pelo Gradle e embutido pelo Xcode
  (monorepo, sem CocoaPods). **[decidir]** SKIE vs. KMP-NativeCoroutines (recomendo SKIE).
- **App iOS**: SwiftUI + Observation (`@Observable`), `NavigationStack` no iPhone e `NavigationSplitView` no iPad,
  Swift Concurrency. Imagens com **Nuke** (cache em disco, pré-carregamento, bom com listas grandes). **[decidir]**
- **iOS mínimo: 17** (Observation, `NavigationSplitView` maduro, `ScrollView` com `scrollPosition`). Cobre a
  imensa maioria dos aparelhos ativos. **[decidir]** (16 é possível, com mais código de compatibilidade).

## 4. Telas (Android → iOS nativo)

| Android | iOS |
|---|---|
| Home (hero "Continue lendo", carrossel de eras, citação, Explore) | Home com título grande, hero, `ScrollView` horizontal com *paging*; no iPad, barra lateral (`NavigationSplitView`) com as seções |
| Eras / Sumário | Lista com imagens; sumário com cabeçalho em paralaxe e botão "Iniciar" |
| Capítulo (ViewPager de páginas, conteúdo misto) | Leitor com `TabView(.page)` ou navegação Anterior/Próximo; blocos renderizados por tipo: parágrafo (HTML simples → `AttributedString` com links), imagem, GIF, vídeo do YouTube (WKWebView), citação, ensaio, listas de filmes/pessoas, bloco especial; coluna de leitura de ~680 pt no iPad |
| Toolbar com idioma + tema | Menu na toolbar (`Menu` com Idioma e Aparência). Idioma do **conteúdo** escolhido no app; o idioma da interface segue o do sistema ou o escolhido por app (iOS já tem "Idioma" por app nos Ajustes) |
| 1001 Filmes, Prêmios (timeline Netflix), Diretores (filtros, Em alta), Linha do tempo, Glossário, Referências | Telas nativas equivalentes; filtros com `Picker(.segmented)`/chips; grades com `LazyVGrid` adaptativo |
| Detalhes de filme/pessoa (TMDB, curiosidades, críticas por idioma, onde assistir) | Telas de detalhe com seções; "Onde assistir" abre o app/serviço; YouTube para filmes livres |
| Onboarding, Apoio, Sobre, Configurações | `sheet`/`fullScreenCover` nativos; Configurações no padrão Ajustes |
| Mini-player + player completo | Mini-player acima da tab/toolbar (estilo Música/Podcasts) + player em `sheet` |

Acessibilidade e qualidade: Dynamic Type em tudo, VoiceOver com rótulos, modo escuro, multitarefa no iPad
(Split View / Stage Manager), teclado e ponteiro no iPad, *haptics* discretos. Regras de largura: `docs/TABLETS.md`.

## 5. Recursos nativos e monetização

- **Compras**: StoreKit 2, três produtos **não consumíveis** (mesmos níveis do Android). Oferta só quando
  `Storefront.current?.countryCode == "BRA"` e conteúdo em pt (mesma regra do `shared/support`).
  "Restaurar compras" obrigatório pela Apple. Termos/privacidade com links na tela de compra (exigência da revisão).
- **Anúncios**: Google Mobile Ads SDK para iOS + UMP (consentimento) + **ATT** (pedido de rastreamento) antes de
  anúncios personalizados. Novo app no AdMob (iOS) e `app-ads.txt` já serve (mesmo publisher).
- **Firebase**: Crashlytics e Analytics no iOS (novo app iOS no projeto Firebase; `GoogleService-Info.plist`).
- **Áudio**: AVPlayer com *Background Modes → Audio*, `MPNowPlayingInfoCenter` (tela de bloqueio, Central de
  Controle, CarPlay/AirPods), `MPRemoteCommandCenter`, download para ouvir offline.
- **Compartilhar**: `ShareLink` com os mesmos links do Android.

## 6. Ponto técnico importante: formato do áudio

Os áudios estão em **Opus dentro de OGG** (`.ogg`), e o **AVPlayer não toca OGG**. Opções:
1. **Gerar uma segunda cópia em AAC/M4A** (`.m4a`, ~48 kbps HE-AAC ou 64 kbps AAC) a partir dos mesmos PCM e
   publicar no mesmo bucket; o manifest ganha o campo do arquivo iOS. Custo de armazenamento ~+0,35 GB (pt), ainda
   dentro do grátis do R2. Sem custo de TTS: o `generate.py` já guarda o PCM em cache — **mas a cache foi apagada
   para liberar disco**; dá para converter direto dos `.ogg` (Opus → AAC) sem perda perceptível em voz. **Recomendado.**
2. Opus em contêiner **CAF** (o iOS toca Opus em CAF desde o iOS 11) — menor, mas menos testado em streaming.
**[decidir]** Recomendo a opção 1 (AAC), simples e 100% suportada (streaming, AirPlay, CarPlay).

## 7. Fases

| Fase | Entrega | Como testar |
|---|---|---|
| **F0 — Base** | Remover `ios/` antigo; criar `shared/` (KMP) e `ios/CinemaHistory` (SwiftUI); workflow de CI em macOS que compila o XCFramework, roda testes e gera o build do simulador | GitHub Actions (macOS, grátis em repo público) |
| **F1 — Núcleo** | Modelos portados, leitura do conteúdo por idioma, conteúdo remoto, regras (idioma, apoio, anúncio no capítulo), manifest de áudio; testes comuns | Testes em JVM + simulador iOS no CI |
| **F2 — MVP iOS** | Home, eras, sumário, leitor de capítulo (todos os tipos de bloco), idioma/aparência, iPhone + iPad | Prints automáticos do simulador no CI (XCUITest) |
| **F3 — Seções** | 1001 Filmes, Prêmios, Diretores, Linha do tempo, Glossário, Referências, Detalhes de filme/pessoa | idem |
| **F4 — Áudio e monetização** | Player com tela bloqueada, StoreKit 2, AdMob + UMP + ATT, Firebase | Sandbox da Apple / TestFlight |
| **F5 — Loja** | Ícones, prints iPhone/iPad (pt/en/es), textos, rótulos de privacidade, revisão da Apple | TestFlight → App Store |
| **F6 (opcional)** | Android passa a usar `:shared` | CI Android atual |

## 8. Limitações do ambiente e o que você precisa ter

- Código Swift e alvos iOS do KMP **só compilam em macOS**. A sessão de trabalho na nuvem é Linux: a compilação, os
  testes e os prints do simulador rodam no **GitHub Actions (macOS)**. Para rodar no seu iPhone/iPad e para o
  envio final, você usa o **Xcode no seu Mac** (ou CI com certificados).
- **Apple Developer Program** (US$ 99/ano) — conta pessoa física ou empresa.
- **App Store Connect**: registro do app, bundle id (sugestão: `com.tiagohs.cinemahistory`), acordo de **Apps
  pagos** + dados bancários/fiscais (necessário para StoreKit), produtos não consumíveis.
- **AdMob**: novo app iOS. **Firebase**: novo app iOS (gerar `GoogleService-Info.plist` novo; o atual é do app antigo).
- Política de privacidade: incluir o iOS (ATT, StoreKit) no site.

## 9. Decisões pendentes

1. iOS mínimo 17? (recomendado)
2. SKIE para a ponte Swift/Kotlin? (recomendado)
3. Nuke para imagens? (recomendado)
4. Áudio iOS em AAC (cópia `.m4a`) — recomendado
5. Android migrar para `:shared` depois do lançamento iOS?
6. Nome na App Store: "História do Cinema" (pt) / "History of Cinema" (en) / "Historia del Cine" (es), como no Android?
