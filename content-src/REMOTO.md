# Atualizar conteúdo do app sem nova versão (Cloudflare)

O app baixa do site (Cloudflare Pages, repo `tiagohs/website`) os arquivos listados em
`https://website-cb5.pages.dev/cinema-history/content/manifest.json`. Hoje: prêmios
(`awards.json`, `awards/nominees/*`, `awards/history/*`) em pt, en e es.

Como funciona no app (`helpers/.../utils/RemoteContent.kt`): na abertura, se passaram 6 h desde a
última conferência, baixa o manifest, compara o sha256 de cada arquivo e baixa só o que mudou para
`filesDir/remote_content/`. O `FakeInterceptor` lê dali antes dos assets. Ao atualizar o app, o cache
é apagado (o conteúdo embutido volta a valer até o próximo sync). Arquivo com hash ou JSON inválido é
ignorado.

## Novo ano de premiação (ex.: Oscar 2027)

1. Crie `content-src/pt/awards/1/2027.md` (formato no topo de `content-src/awards.py`; regras em
   `content-src/PREMIOS.md`) e acrescente as fontes em `content-src/pt/awards/1/FONTES_2027.md`.
2. `python3 content-src/awards.py check 1` até não haver ERRO; depois `python3 content-src/awards.py build 1`.
3. Traduza: `python3 content-i18n/i18n.py export --lang en --groups awards` (e `es`), preencha os lotes,
   `import`, `apply --lang en|es` e `validate`.
4. Publique: `python3 content-src/remote.py --site <pasta do repo website>` e faça commit/push do site.
   Em até ~6 h os aparelhos com o app 2.5.0+ recebem o conteúdo novo.
5. Faça commit no repo do app também (a próxima versão já sai com o conteúdo embutido).

## Estender para outro conteúdo

Acrescente o padrão em `PUBLISH` (`content-src/remote.py`). O app já procura qualquer arquivo de
conteúdo no cache remoto; não precisa mudar código. Se o formato do JSON mudar de um jeito que
versões antigas não entendam, suba `MIN_APP_VERSION`.
