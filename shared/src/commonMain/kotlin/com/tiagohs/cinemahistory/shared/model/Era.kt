package com.tiagohs.cinemahistory.shared.model

/** Citação de abertura de uma era. */
data class CitacaoEra(val texto: String, val autor: String)

/**
 * Uma das 8 eras da História do Cinema.
 * [cor] é o nome da cor de acento no JSON do Android (ex.: "md_red_500"); o app iOS a troca pelo token da era
 * (a cor da era é só acento, nunca fundo de tela).
 */
data class Era(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val descricao: String,
    val cor: String,
    val nova: Boolean,
    val bloqueada: Boolean,
    val imagem: String?,
    val citacao: CitacaoEra?,
)
