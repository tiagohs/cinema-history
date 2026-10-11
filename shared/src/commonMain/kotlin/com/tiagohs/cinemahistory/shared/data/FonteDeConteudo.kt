package com.tiagohs.cinemahistory.shared.data

import com.tiagohs.cinemahistory.shared.model.Idioma

/**
 * Onde o núcleo lê cada arquivo do conteúdo, na mesma ordem do Android (FakeInterceptor):
 * 1. versão baixada do site (cache do conteúdo remoto), no idioma pedido;
 * 2. arquivo embutido no app, no idioma pedido;
 * 3. arquivo embutido em português (idioma de origem), se faltar tradução.
 */
class FonteDeConteudo(private val embutido: ContentSource, private val remoto: ContentSource? = null) {

    fun ler(idioma: Idioma, caminho: String): String? {
        val noIdioma = "${idioma.pasta}/$caminho"
        remoto?.lerTexto(noIdioma)?.let { return it }
        embutido.lerTexto(noIdioma)?.let { return it }
        return if (idioma != Idioma.PT) embutido.lerTexto("${Idioma.PT.pasta}/$caminho") else null
    }
}
