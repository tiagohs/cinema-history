# Tradução do conteúdo (assets/local)

O conteúdo do app (eras, páginas, prêmios, timelines, glossário, referências) fica em
`android/app/src/main/assets/local/<idioma>/`. **O português (`pt/`) é a única fonte de verdade.**
As pastas `en/` e `es/` são **geradas** por este pipeline e nunca devem ser editadas à mão.

## Como funciona

1. **Extração** – só os campos de texto (título, descrição, citação, legenda, créditos etc.) são
   extraídos. Ids, links, vídeos, imagens, cores e enums nunca passam pela tradução.
2. **Marcadores** – tags HTML viram marcadores: `<a href="https://_{...}">Título</a>` vira
   `<t1>Título</t1>`, `<br/>` vira `<t2/>`. O tradutor só vê os marcadores; os links originais
   são restaurados a partir do português.
3. **Títulos de filmes** – nomes de filmes em listas de indicados e nos especiais são
   preenchidos automaticamente com o título oficial do TMDB em cada idioma (cache em `tmdb/titles.json`).
   Nos textos, o lote de tradução traz os títulos oficiais dos filmes citados como referência.
4. **Catálogo** – as traduções ficam em `translations/<idioma>.json`, por id do texto
   (hash do texto em pt). Se o texto em pt mudar, a tradução fica marcada como desatualizada.
5. **Geração e validação** – `apply` gera a pasta do idioma; `validate` garante que a estrutura,
   ids, links e quebras de linha são idênticos ao pt. O CI roda a validação e o teste
   `LocalContentTest` (que lê tudo com o mesmo Gson do app).

## Comandos (na raiz do repositório)

```bash
python3 content-i18n/i18n.py status --lang en            # cobertura por grupo
python3 content-i18n/i18n.py tmdb                        # atualiza títulos oficiais (cache)
python3 content-i18n/i18n.py export --lang en --groups maintopics,awards --max-chars 40000
#   -> content-i18n/work/en-batch.json: preencher o campo "t" de cada item
python3 content-i18n/i18n.py import --lang en content-i18n/work/en-batch.json
python3 content-i18n/i18n.py apply --lang en             # gera assets/local/en
python3 content-i18n/i18n.py validate --lang en
```

## Regras para quem traduz

- Manter todos os marcadores `<tN>…</tN>` e `<tN/>` (pode mudar a posição, não pode perder/duplicar).
- Manter as quebras de linha (`\n`).
- Títulos de filmes: usar o título oficial do idioma (campo `movies` do lote). No padrão
  `<t1>Viskningar och rop</t1> (BR: Gritos e sussurros, 1972)`, trocar o "(BR: …)" pelo título
  oficial do idioma: `(Cries and Whispers, 1972)`; se o título oficial for igual ao original, remover o parêntese
  e manter só o ano: `(1972)`.
- Textos já no idioma de destino (ex.: títulos de vídeos do YouTube em inglês) ficam como estão.
- Nomes de pessoas, canais e veículos não são traduzidos.
- Espanhol: variante latino-americana neutra.

## Liberar um idioma no app

Quando `status` mostrar 100% e a revisão estiver feita:
1. `apply --lang <idioma> --require-complete` e `validate --lang <idioma>`;
2. adicionar o idioma em `ContentLanguage.ENABLED` (helpers);
3. ativar `androidResources { generateLocaleConfig = true }` para aparecer no seletor do Android 13+.
