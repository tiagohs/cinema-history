package com.tiagohs.entities.awards

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.NomineeType
import java.io.Serializable

/**
 * Uma entrada de `awards/nominees/<id>/index.json`: o resumo de um ano da premiação.
 * O conteúdo completo do ano (categorias, vídeos, júri...) fica em `<id>/<ano>.json`
 * e só é carregado quando o ano é aberto.
 */
data class AwardYearSummary(
    @SerializedName("year")
    val year: String,

    @SerializedName("categories")
    val categories: Int? = null,

    @SerializedName("nominees")
    val nominees: Int? = null,

    @SerializedName("highlight")
    val highlight: AwardHighlight? = null
) : Serializable

/** O vencedor principal do ano (ex.: Melhor Filme), usado no destaque do topo da tela. */
data class AwardHighlight(
    @SerializedName("category")
    val category: String? = null,

    @SerializedName("type")
    val type: NomineeType? = NomineeType.MOVIE,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("image_path")
    val imagePath: String? = null,

    @SerializedName("director")
    val director: String? = null,

    @SerializedName("backdrop_path")
    val backdropPath: String? = null
) : Serializable
