package com.tiagohs.helpers.utils

/**
 * Arquivos do app hospedados fora do APK (imagens das timelines, futuros JSONs dinâmicos).
 *
 * Ficam no site (Cloudflare Pages, repositório tiagohs/website) em /cinema-history/media/.
 * Para trocar de domínio (ex.: domínio próprio), basta mudar BASE_URL.
 */
object MediaUrls {

    const val BASE_URL = "https://website-cb5.pages.dev/cinema-history/media/"

    /** "images/img_x.webp" -> URL completa. */
    fun media(path: String): String = BASE_URL + path.trimStart('/')
}
