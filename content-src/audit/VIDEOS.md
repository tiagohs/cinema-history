# Substituir vídeos quebrados do app Cinema History

O app incorpora vídeos do YouTube nos capítulos. Alguns saíram do ar (404), não permitem mais incorporação (401)
ou estão bloqueados (403). Você recebe uma lista (`videos_todo_N.json`) com, para cada vídeo quebrado: arquivo,
id antigo, título e legenda que o app mostra (`title`, `text`), canal citado (`source`) e a seção/verbete (`section`).

Para cada item:
1. Entenda o que o vídeo era (título + legenda + canal + seção). Se precisar de mais contexto, abra o arquivo em
   `android/app/src/main/assets/local/pt/<file>` e procure o id antigo.
2. Busque um substituto: `python3 content-src/yt_search.py "<busca>"` (só mostra resultados reais; "OK" = existe e
   permite incorporação, "XX" = não use). Tente primeiro o MESMO vídeo republicado (mesmo título, mesmo canal ou
   canal oficial), depois um equivalente: trailer oficial, cena oficial, o mesmo ensaio em outro canal oficial,
   o filme completo em domínio público (canais confiáveis), reportagem equivalente.
3. Prefira canais oficiais (estúdios, distribuidoras, Oscars, festivais, museus, emissoras) e o criador original.
   Evite reuploads de terceiros de material com direitos, vídeos curtos de baixa qualidade e compilações aleatórias.
4. Confirme com `python3 content-src/tmdb.py yt <ID>` (mostra título e canal reais).

Escreva o resultado em `content-src/audit/videos_map_N.json` (N = o mesmo da sua lista), uma lista com um objeto por
item, nesta forma:
```
{"old": "<id antigo>", "new": "<id novo ou null>", "new_title": "<título real do vídeo novo>",
 "new_channel": "<canal real>", "source": "Youtube: Canal <i>Nome do canal</i>",
 "caption_title": "<novo título para o app em português, só se o antigo não servir mais; senão null>",
 "confidence": "alta|média|baixa", "note": "<curto: por que esse vídeo; ou por que não achou>"}
```
- `new: null` quando não houver substituto bom: diga na nota o que procurar (o Tiago pode procurar).
- Não edite nenhum outro arquivo. Responda com um resumo: quantos substituídos (alta/média/baixa) e quantos sem substituto.
