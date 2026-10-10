# Página da Google Play — v2 (História do Cinema 2.5.0)

Novas artes e textos da loja, com fundo escuro cinematográfico, Oswald e Proxima Nova (fontes do app), destaque dourado e um benefício por imagem. A base de pesquisa está em [PESQUISA.md](PESQUISA.md).

## O que tem em cada pasta

| Caminho | Conteúdo |
|---|---|
| `PESQUISA.md` | Regras atuais da Play, o que os apps de topo fazem, conclusões e fontes. |
| `textos/pt-BR.md`, `en-US.md`, `es-419.md` | Título, descrição curta, descrição longa (com contagem de caracteres), legendas e alt text de cada imagem. |
| `textos/legendas.json` | **Fonte das legendas usadas na arte.** Edite aqui e renderize de novo. `*palavra*` sai em dourado. |
| `templates/` | `screenshot.html`, `feature.html`, `base.css` e `fonts/` (Oswald e Proxima Nova copiadas do app). |
| `render.py` | Gera todos os PNGs com Playwright/Chromium. |
| `screens-raw/` | **Coloque aqui os prints novos do app** (veja abaixo). |
| `feature-graphic/<idioma>.png` | Feature graphic 1024×500 (PNG 24 bits, sem alfa). |
| `screenshots/<idioma>/01..08.png` | Screenshots de celular 1080×1920 (PNG 24 bits, sem alfa). |

### As 8 imagens

| # | Benefício | Prints usados (`screens-raw/`) |
|---|---|---|
| 01 | 130 anos de cinema em 8 eras | `era` (atrás) + `home` (frente) |
| 02 | Capítulos com texto e vídeo | `capitulo-video` + `capitulo` |
| 03 | Premiações estilo streaming | `premios-ano` + `premios` |
| 04 | Linha do tempo animada | `timeline` |
| 05 | 1001 filmes | `1001` |
| 06 | Filmes e pessoas em detalhes | `pessoa` + `filme` |
| 07 | Glossário e referências | `referencias` + `glossario` |
| 08 | Três idiomas | `home` em cada idioma (veja abaixo) |

## Prints novos

Nomes esperados em `screens-raw/`: `home`, `era`, `capitulo`, `capitulo-video`, `premios`, `premios-ano`, `timeline`, `1001`, `filme`, `pessoa`, `glossario`, `referencias` (.png, .jpg ou .webp).

Ordem de busca para cada print:
1. `screens-raw/<idioma>/<nome>.png`, por exemplo `screens-raw/en-US/home.png`. Use isso para ter prints do app em inglês e espanhol nas versões em inglês e espanhol, que é o recomendado pela Play.
2. `screens-raw/<nome>.png`, usado em todos os idiomas.
3. Se nenhum dos dois existir, um **print antigo de `/screens`** (fallback). O script recorta a barra antiga de status e de navegação e desenha uma barra limpa. **Os fallbacks são só para desenvolver, porque mostram a interface antiga.** Prints antigos também deixam uma faixa preta no fim da tela do celular, já que são mais baixos que um celular atual.

A imagem 08 mostra a `home` de `screens-raw/pt-BR/`, `screens-raw/en-US/` e `screens-raw/es-419/`, com etiquetas PT, EN e ES. Sem esses prints, aparece a mesma `home` três vezes.

Dicas para capturar:
- Use um celular ou emulador 1080×2400 (ou parecido), em tela cheia e com o tema que for destaque (escuro combina mais com a arte).
- Deixe a barra de status limpa com o modo demo:
  ```
  adb shell settings put global sysui_demo_allowed 1
  adb shell am broadcast -a com.android.systemui.demo -e command enter
  adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200
  adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false
  adb shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 -e mobile show -e datatype none -e level 4
  adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
  adb exec-out screencap -p > home.png
  ```
- Para cortar a barra de gestos ou a de status dos prints novos, ajuste `RAW_CROP_TOP` e `RAW_CROP_BOTTOM` no topo de `render.py`.
- Marcas de terceiros: evite que logos ou estatuetas sejam o elemento principal do print (por exemplo, prefira um termo do glossário sem o logotipo da Disney e uma tela de prêmios com a lista de vencedores em vez da estatueta). Detalhes em PESQUISA.md, item 9.

## Como renderizar de novo

```bash
# na raiz do repositório
pip install playwright pillow            # se ainda não tiver
export PLAYWRIGHT_BROWSERS_PATH=/opt/pw-browsers   # ou: python3 -m playwright install chromium
python3 content/v2/render.py             # tudo: 3 feature graphics + 24 screenshots (~1 min)
python3 content/v2/render.py --lang en-US --only 01,03   # só alguns
python3 content/v2/render.py --size 1080x2160            # mais altos (2:1 é o máximo da Play; 1080x2340 é recusado)
```

O script lista no terminal qual print entrou em cada imagem e marca `FALLBACK` quando usou um print antigo. Os PNGs saem em RGB, sem canal alfa, como a Play exige.

## Checklist do Play Console

Caminho: **Play Console → História do Cinema → Crescimento → Presença na loja → Página principal da loja** (*Main store listing*). Para outros idiomas, use **Gerenciar traduções** e cadastre pt-BR, en-US e es-419. Se quiser, adicione es-ES e outros espanhóis com o mesmo material de es-419.

Para cada idioma (pt-BR, en-US, es-419):

- [ ] **Nome do app**: `textos/<idioma>.md`, seção "Título" (até 30).
- [ ] **Descrição curta**: seção "Descrição curta" (até 80).
- [ ] **Descrição completa**: bloco entre os marcadores (até 4000). Cole sem as crases.
- [ ] **Ícone do app**: não muda (512×512 atual).
- [ ] **Gráfico de recursos**: `feature-graphic/<idioma>.png` + alt text do .md.
- [ ] **Capturas de tela do telefone**: `screenshots/<idioma>/01.png` a `08.png`, **nesta ordem**, + alt text de cada uma.
- [ ] Tablet 7" e 10" (opcional): se quiser preencher, renderize com `--size 1080x1920` e suba as mesmas 4 primeiras (a Play aceita 9:16 em tablets). O ideal, porém, é ter prints reais de tablet.
- [ ] Vídeo do YouTube (opcional): se tiver, o feature graphic vira a capa e recebe um botão de play no centro. A arte já deixa o centro livre de texto pequeno.
- [ ] Salvar → **Enviar alterações para revisão** (em *Visão geral da publicação*).
- [ ] Opcional: rode um **experimento da página da loja** (*Experimentos da página de detalhes*) comparando o título atual com a alternativa com palavra-chave, ou a primeira imagem atual com outra ordem.

Antes de enviar:
- [ ] Os screenshots usam prints **novos** (o terminal não mostra `FALLBACK`).
- [ ] Os prints não têm notificações ou nome da operadora.
- [ ] Nenhum texto diz "melhor", "nº 1", "grátis", "novo" ou preço.
