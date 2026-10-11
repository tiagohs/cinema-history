package com.tiagohs.cinemahistory.shared.model

/** Idiomas do conteúdo (UC-43). */
enum class Idioma(val pasta: String) {
    PT("pt"), EN("en"), ES("es");

    companion object {
        /** Idioma de origem: sempre existe e é o fallback dos arquivos sem tradução. */
        val ORIGEM = PT

        fun doCodigo(codigo: String): Idioma? = when (codigo.lowercase().substringBefore('-').substringBefore('_')) {
            "pt" -> PT
            "es" -> ES
            "en" -> EN
            else -> null
        }

        /** Um código de aparelho só: pt e es valem; qualquer outro cai em inglês. */
        fun doAparelho(codigo: String): Idioma = doCodigo(codigo) ?: EN

        /**
         * Regra do Android (ContentLanguage.current): 1º o idioma escolhido no app, 2º os idiomas do aparelho
         * na ordem de preferência; o primeiro que for pt, en ou es vence; nenhum → inglês.
         */
        fun resolver(escolhaDoApp: String?, preferidosDoAparelho: List<String>): Idioma =
            (listOfNotNull(escolhaDoApp) + preferidosDoAparelho).firstNotNullOfOrNull { doCodigo(it) } ?: EN

        /** Idioma pedido ao TMDB (títulos, sinopses, vídeos): espanhol latino-americano como no Android. */
        fun tagDoTmdb(idioma: Idioma): String = when (idioma) {
            PT -> "pt-BR"
            ES -> "es-MX"
            EN -> "en-US"
        }
    }
}
