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
- **Segmento** `{id, speaker, original, text, chars}`
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
            "marks":[{"seg":"00-01","t":0.25},{"seg":"00-02","t":8.9}]}],
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
- **Versionamento**: o app compara o `hash` de cada faixa no manifest para saber o que baixar de novo. Se a voz ou o formato mudar, publique em `v2/`.

## 6. Como o app usaria (esboço)

- **Player**:
  - Use Media3 ExoPlayer, que toca ogg/opus nativamente no Android, dentro de um `MediaSessionService`.
  - Cada faixa vira um `MediaItem`, com o título como metadado. A lista de faixas é a playlist do capítulo, e cada faixa é uma "subcategoria".
  - O `MediaSession` cuida da tela bloqueada, da notificação, dos fones Bluetooth, do Android Auto e do foco de áudio.
- **Tela do capítulo**: um botão "Ouvir" abre um mini player com a lista de faixas (Abertura, partes, "Saiba mais", "Vale a conferida"), anterior e próxima, ±15 s e velocidade de 0,75× a 2× com `player.setPlaybackSpeed`. A posição fica salva por capítulo.
- **Destaque do parágrafo**:
  - Cada `segments[].id` do roteiro corresponde a um parágrafo, e o manifest já traz `marks` com o segundo em que cada segmento começa.
  - O app compara `currentPosition` com essas marcas para rolar e destacar o parágrafo na tela.
  - Para o app achar o parágrafo, ligue `segments[].original` ao texto do JSON do capítulo, ou exporte junto um índice segmento → posição na página.
- **Offline**:
  - Use o `DownloadManager` do Media3 com `CacheDataSource` para baixar por faixa ou o capítulo inteiro.
  - Mostre o tamanho antes de baixar. Um capítulo tem 5 a 15 MB.
  - Em streaming, o ExoPlayer já faz cache progressivo.
- **Descoberta**: o app baixa `manifest.json` do capítulo para saber quais faixas existem e a duração de cada uma. Se não houver manifest, o botão "Ouvir" fica escondido. Assim dá para lançar o áudio aos poucos, por idioma ou por era.
