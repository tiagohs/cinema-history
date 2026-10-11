package com.tiagohs.cinemahistory.shared.model

/** Idiomas do conteúdo (UC-43). Qualquer idioma de aparelho que não seja pt ou es cai em inglês. */
enum class Idioma(val pasta: String) {
    PT("pt"), EN("en"), ES("es");

    companion object {
        fun doAparelho(codigo: String): Idioma = when (codigo.lowercase().substringBefore('-').substringBefore('_')) {
            "pt" -> PT
            "es" -> ES
            else -> EN
        }
    }
}
