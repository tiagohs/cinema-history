# Narração em áudio dos capítulos

Tudo pronto para gerar o áudio no futuro. Os roteiros já estão gerados. Nenhum áudio foi gerado e nenhuma API foi chamada.

## Arquivos

| Arquivo | O que é |
|---|---|
| `scripts.py` | Lê os JSONs do app (pt, en, es) e gera os roteiros de narração por capítulo. |
| `normalize.py` | Regras de texto para fala: tira o HTML e o "(BR: …)", escreve números, datas, moedas, siglas e algarismos romanos por extenso e marca a pronúncia de nomes. |
| `roteiros/<idioma>/main_<era>/page_<n>.json` | Roteiro do capítulo, dividido em faixas e segmentos. É a entrada do `generate.py`. |
| `roteiros/<idioma>/main_<era>/page_<n>.txt` | O mesmo roteiro em versão legível, para revisar. |
| `roteiros/resumo.csv` | Uma linha por capítulo: idioma, era, página, título, faixas, caracteres e minutos estimados. |
| `generate.py` | Gera o áudio com Chirp 3 HD ou Gemini TTS. Tem os modos `--dry-run` (custo) e `--sample` (amostra de vozes). |
| `make_test_assets.py` | Gera a fonte de teste do app (tons sintéticos + manifests) em `android/app/src/debug/assets/audio-test/`. Não chama nenhuma API. |
| `vozes.json` | Vozes de narrador e de citação por idioma e provedor, mais as pausas e o modo de pronúncia. **São sugestões: confirme ouvindo a amostra.** |
| `out/` | O áudio gerado: `out/<idioma>/main_<era>/page_<n>/<faixa>.ogg` e um `manifest.json` por capítulo. |
| `cache/` | Cache por segmento (`<hash>.pcm`). Não apague: é ele que evita pagar de novo pelo que não mudou. |

### Estrutura do roteiro

- **Capítulo** `{era, page, title, tracks, chars, est_minutes}`
- **Faixa** `{id, title, kind, segments, chars, est_minutes}`. As faixas de um capítulo são:
  - `00` Abertura: nome da era, título e resumo do capítulo.
  - Uma faixa por bloco de texto corrido. Um bloco são os parágrafos entre dois elementos visuais.
    - Um bloco com menos de 700 caracteres é juntado ao bloco anterior.
    - Um bloco com mais de 2.500 caracteres é quebrado no fim de um parágrafo.
    - O título da faixa vem do primeiro `<strong>` ou link do bloco. Quando não há nenhum, vira "Parte N".
  - "Saiba mais: …": cada texto interno (`block_special`) vira uma faixa própria.
  - "Vale a conferida": a última faixa, com os títulos da `movie_list`.
- **Segmento** `{id, speaker, source_index, original, text, chars}`
  - `source_index` é o índice, no `content_list` do JSON do capítulo, do item de onde o segmento veio. A abertura usa `-1`. "Saiba mais" aponta para o `block_special`, e "Vale a conferida" para a primeira `movie_list`. É o que o app usa para destacar o parágrafo lido.
  - `speaker` é `narrador` ou `citacao`. A citação é lida pela voz secundária, e depois o narrador diz "disse Fulano em Filme."
  - `original` é o texto limpo, sem HTML.
  - `text` é o texto que será falado.
  - `[[pron: Méliès|Meliés]]` marca um nome que o TTS costuma errar. Em `vozes.json`, `"pron": "original"` manda o nome como está escrito e `"respell"` manda a grafia fonética. Teste os dois na amostra. A lista de nomes fica em `normalize.py`, no dicionário `PRON`.
- **Estimativa de duração**: caracteres por minuto (pt 900, en 1000, es 950). A duração real só aparece no `manifest.json` depois de gerar o áudio.

## 1. Gerar ou atualizar os roteiros (grátis)

```bash
python3 content-src/audio/scripts.py                 # os 3 idiomas, todos os capítulos
python3 content-src/audio/scripts.py --langs pt --era 8 --page 3   # só um capítulo
```

Rode de novo sempre que um capítulo mudar. Depois revise o `.txt`. Se algo soar errado, ajuste as regras em `normalize.py`, nunca o roteiro à mão, porque a próxima execução sobrescreve o roteiro.

## 2. Estimar o custo (não chama a API)

```bash
python3 content-src/audio/generate.py --provider chirp  --dry-run
python3 content-src/audio/generate.py --provider gemini --dry-run --langs pt
```

O `--dry-run` mostra o total e o que ainda falta gerar, ou seja, o que não está no cache.

### Estimativa atual (calculada pelos roteiros em 09/10/2026)

| Idioma | Capítulos | Faixas | Caracteres | Horas (est.) | Chirp 3 HD (US$ 30/M) | Chirp com 1 M grátis no mês | Gemini (~US$ 0,90/h) |
|---|---:|---:|---:|---:|---:|---:|---:|
| pt | 95 | 988 | 1.212.065 | 22,4 | US$ 36,36 | US$ 6,36 | US$ 20,20 |
| en | 95 | 943 | 1.122.550 | 18,7 | US$ 33,68 | US$ 3,68 | US$ 16,84 |
| es | 95 | 1.010 | 1.266.607 | 22,2 | US$ 38,00 | US$ 8,00 | US$ 20,00 |
| **Total** | 285 | 2.941 | **3.601.222** | **63,4** | **US$ 108,04** | **US$ 18,04** (um idioma por mês) | **US$ 57,04** |

- **Chirp 3 HD**: US$ 30 por milhão de caracteres, e o primeiro milhão de cada mês é grátis. Gerar um idioma por mês sai por cerca de US$ 18 no total.
- **Gemini TTS**: a estimativa usa US$ 0,90 por hora de áudio. Pela documentação de out/2026, o `gemini-3.8-flash-tts` cobra US$ 9 por milhão de tokens de áudio (25 tokens por segundo).
  - Até 31/12/2026, isso dá cerca de US$ 0,81/h, ou ~US$ 51 no total.
  - A partir de 2027, o preço dobra para ~US$ 1,62/h, ou ~US$ 103 no total.
  - O `gemini-3.8-flash-lite-tts` é mais barato.
  - O Gemini TTS não tem cota grátis.
- Os preços mudam. Confira antes de gerar:
  - https://cloud.google.com/text-to-speech/pricing
  - https://ai.google.dev/gemini-api/docs/pricing

## 3. Gerar uma amostra de vozes (custa centavos)

```bash
export GOOGLE_TTS_API_KEY=...        # chave de API com a Cloud Text-to-Speech API ativada
python3 content-src/audio/generate.py --provider chirp  --sample pt 1 1 --voices Charon,Iapetus,Sadaltager,Kore,Sulafat

export GEMINI_API_KEY=...
python3 content-src/audio/generate.py --provider gemini --sample pt 1 1 --voices Charon,Kore,Sulafat
```

- A amostra tem cerca de 2.300 caracteres por voz: a abertura, o começo do texto e uma citação. Com 5 vozes no Chirp, isso dá cerca de US$ 0,35, ou nada dentro da cota grátis.
- Os arquivos ficam em `out/amostras/<provedor>/<idioma>/`.
- Escolha as vozes e coloque os nomes em `vozes.json`, em `narrador` e `citacao`.
- Para testar a pronúncia, troque `"pron"` para `"respell"` e gere a amostra de novo.

## 4. Gerar tudo

```bash
python3 content-src/audio/generate.py --provider chirp --langs pt              # um idioma
python3 content-src/audio/generate.py --provider chirp --langs pt --era 8      # uma era
python3 content-src/audio/generate.py --provider chirp                         # tudo
```

- O script pode ser interrompido e retomado. O que já está no cache não é cobrado de novo.
- Se um capítulo mudar, rode `scripts.py` e depois o `generate.py`. Só os segmentos alterados vão para a API. As faixas são remontadas e o `manifest.json` é atualizado.
- Faixas que deixaram de existir são apagadas.
- Cada faixa é um arquivo Opus em ogg a 48 kbps, mono, com os segmentos juntados e uma pausa entre eles. As pausas ficam em `vozes.json`, em `pausas_ms`.
- **Dependências**:
  - Python 3.9 ou mais novo, só com a biblioteca padrão. O `requests` não é necessário.
  - `ffmpeg` com libopus no PATH. Instale com `sudo apt install ffmpeg` ou `brew install ffmpeg`.
- **Autenticação do Chirp**: use `GOOGLE_TTS_API_KEY`, ou `GOOGLE_ACCESS_TOKEN=$(gcloud auth print-access-token)` junto com `GOOGLE_CLOUD_PROJECT`.
- **Autenticação do Gemini**: use `GEMINI_API_KEY`.
- O endpoint do Gemini é `v1beta/interactions`, que é novo. Se a API mudar, ajuste só a classe `Gemini` em `generate.py`.

### manifest.json (por capítulo)

```json
{"lang":"pt","era":1,"page":1,"title":"Visionários","provider":"chirp",
 "voices":{"narrador":"pt-BR-Chirp3-HD-Charon","citacao":"pt-BR-Chirp3-HD-Gacrux"},
 "tracks":[{"id":"00","title":"Abertura","kind":"open","file":"00.ogg","duration_s":21.4,"bytes":131072,"chars":284,"hash":"…",
            "marks":[{"seg":"00-01","t":0.25,"source_index":-1},{"seg":"00-02","t":8.9,"source_index":-1}]}],
 "duration_s":640.2}
```

## 5. Onde hospedar: Cloudflare R2 (recomendado)

- **Tamanho**: 48 kbps dá cerca de 21,6 MB por hora. Os 3 idiomas somam ~63 h, ou **~1,4 GB**.
- **Custo**:
  - O R2 dá 10 GB de armazenamento grátis por mês. Acima disso, custa US$ 0,015 por GB por mês.
  - A saída de dados (egress) é grátis.
  - As leituras são grátis até 10 milhões por mês. Acima disso, custam US$ 0,36 por milhão.
  - **Na prática, o custo é US$ 0.**
- **Passos**:
  1. Crie o bucket `cinema-history-audio` no painel da Cloudflare, em R2.
  2. Ligue um **domínio próprio** ao bucket, por exemplo `audio.<seu-domínio>`, em Settings → Custom Domains. O domínio `r2.dev` serve só para testes, porque tem limite de taxa.
  3. Envie a pasta `out/<idioma>/` mantendo os caminhos:
     ```bash
     rclone copy content-src/audio/out/pt r2:cinema-history-audio/v1/pt --exclude 'amostras/**'
     ```
     O `wrangler r2 object put` também funciona, mas envia um arquivo por vez.
  4. A URL pública de uma faixa fica assim: `https://audio.<seu-domínio>/v1/pt/main_1/page_1/01.ogg`, com o manifest em `…/page_1/manifest.json`.
  5. Configure o cache: `Cache-Control: public, max-age=31536000` nos `.ogg` e um tempo curto no `manifest.json`.
- **Versionamento**: o `hash` de cada faixa no manifest muda quando o áudio muda. Se a voz ou o formato mudar, publique em `v2/` e troque a `BASE_URL` do app (ver "No app").
- `marks[].source_index` não entra no `hash` (não muda o áudio). Se só os índices mudarem, rode `scripts.py` e `generate.py` de novo: o script atualiza as marcas do manifest sem chamar a API.

## 6. No app

O código fica em `android/app/src/main/java/com/tiagohs/cinema_history/audio/`.

### Como o app consome os arquivos

- **Endereço**: tudo sai de `AudioConfig.BASE_URL`, o único lugar a trocar. Hoje ele vale `https://audio.example.invalid/cinema-history/`, com um `TODO(R2)`.
  - Troque pelo domínio do bucket, terminando com `/`. Exemplo: `https://audio.<seu-domínio>/v1/`.
  - O app monta `<BASE_URL><idioma>/main_<era>/page_<n>/manifest.json`, que é a mesma estrutura de `out/`.
  - O idioma vem de `ContentLanguage.current()`: pt, en ou es.
- **Descoberta**: ao abrir um capítulo, o app baixa o `manifest.json` (`AudioRepository`).
  - Se o manifest não existir (404), der erro ou não houver rede, o botão "Ouvir" não aparece.
  - O resultado fica em memória: o positivo por 6 h e o negativo por 10 min.
  - Assim dá para publicar o áudio aos poucos, por idioma, era ou capítulo, sem atualizar o app.
- **Faixas**: o `file` de cada faixa é resolvido em relação à pasta do capítulo. Pode ser `01.ogg` ou um caminho relativo. Cada faixa vira um `MediaItem` com `mediaId` `<idioma>/<era>/<página>#<faixa>`.
- **Destaque do texto**:
  - O app compara a posição do player com `marks[].t` da faixa atual e pega o `source_index` do último segmento que já começou.
  - O item correspondente do `content_list` ganha um fundo suave, e a lista rola até ele.
  - "Ouvir a partir daqui" faz o caminho inverso: `source_index` → primeiro segmento → faixa e posição.
  - Os índices valem para o `content_list` dos JSONs locais (`assets/local/<idioma>/pages`). Se um capítulo mudar, rode `scripts.py` e `generate.py` de novo.
- **Player**: Media3 (`AudioPlaybackService`, um `MediaSessionService`). Ele cuida de:
  - notificação e tela bloqueada, com voltar e avançar 15 s;
  - fones e Bluetooth, e pausa ao desconectar o fone;
  - foco de áudio;
  - velocidade de 0,75× a 2×;
  - timer de sono (15 a 60 min ou fim da faixa);
  - "Ouvir a era inteira", que encadeia os capítulos seguintes que têm áudio, com a página acompanhando.
  - A posição é salva por capítulo em `SharedPreferences` (`audio_positions`), e o app retoma de onde parou.
- **Offline**: "Baixar para ouvir offline" grava as faixas no cache do Media3 (`SimpleCache` em `files/audio/cache`, sem despejo automático), usando um Worker do WorkManager.
  - O manifest fica em `files/audio/manifests/`.
  - O streaming normal não grava no cache, então o espaço só cresce com downloads pedidos pelo usuário.
- **Acesso** (`support/Supporter.kt`):
  - Sem oferta e sem apoio: nenhum controle de áudio.
  - Com oferta e sem apoio: o botão aparece com cadeado. A faixa `00` (Abertura) toca grátis. Ao terminar, ou ao pedir outra faixa, abre `Supporter.openSupportScreen(activity, "audio")`.
  - Apoiador: tudo liberado. Uma compra libera o áudio na hora, via `Supporter.addListener`.
- **Anúncios**: o mini-player some quando o anúncio nativo do capítulo passa perto dele. Nenhum controle de áudio fica sobre o anúncio ou colado nele.

### Onde trocar a BASE_URL

`android/app/src/main/java/com/tiagohs/cinema_history/audio/AudioConfig.kt` → `BASE_URL`. Não há outro lugar.

### Como testar sem áudio real (build debug)

1. Gere a fonte de teste. Ela já está no repositório, então só é preciso rodar de novo se os roteiros mudarem:
   ```bash
   python3 content-src/audio/make_test_assets.py      # era 1, capítulos 1 e 2, em pt/en/es (~120 KB)
   ```
   - O script cria tons sintéticos em `android/app/src/debug/assets/audio-test/tones/`.
   - Também cria um manifest por capítulo, montado a partir do roteiro real, com todas as faixas e marcas espalhadas pela duração do tom.
   - Esses arquivos só entram no APK de debug.
2. Instale o debug: `./gradlew :app:installDebug`.
3. Abra Eras → Era 1 → Capítulo 1. No menu ⋮ da página, toque em **Áudio (debug)**:
   - **Fonte de teste**: liga ou desliga `audio_test_source`, para usar `asset:///audio-test/` no lugar da `BASE_URL`.
   - **Apoio: …**: simula "sem oferta", "cadeado" ou "apoiador" sem mexer no `Supporter`. "Comportamento real" volta a usar o `Supporter`.
   - A tela é recriada para aplicar a escolha.
4. Com "Fonte de teste" e "Apoio: apoiador", teste:
   - o botão "Ouvir · N min" no cabeçalho;
   - o mini-player acima do rodapé;
   - o player expandido;
   - o destaque do parágrafo e o chip "Voltar ao trecho";
   - o toque longo → "Ouvir a partir daqui";
   - "Ouvir a era inteira", que passa do capítulo 1 para o 2;
   - o download.
5. Com "Apoio: oferta, não apoia", só a Abertura toca. Ao terminar, o app chama `openSupportScreen`.
