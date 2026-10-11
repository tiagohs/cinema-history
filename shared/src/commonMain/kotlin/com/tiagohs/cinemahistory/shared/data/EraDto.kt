package com.tiagohs.cinemahistory.shared.data

import com.tiagohs.cinemahistory.shared.model.CitacaoEra
import com.tiagohs.cinemahistory.shared.model.Era
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Entrada de `maintopics.json`; o arquivo mistura eras com outros tipos (ex.: anúncios), por isso quase tudo é opcional. */
@Serializable
internal data class MainTopicDto(
    @SerialName("main_topic_type") val tipo: String? = null,
    val id: Int,
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val color: String? = null,
    @SerialName("is_new") val isNew: Boolean = false,
    val blocked: Boolean = false,
    val image: ImageDto? = null,
    // Nas eras `quote` é um objeto {quote, author}; nos cartões de citação do Início é uma string.
    val quote: JsonElement? = null,
)

@Serializable
internal data class ImageDto(val url: String? = null)

internal const val TIPO_HISTORIA = "history_cinema"

internal fun MainTopicDto.paraEra(): Era? {
    if (tipo != TIPO_HISTORIA) return null
    return Era(
        id = id,
        titulo = title.orEmpty(),
        subtitulo = subtitle.orEmpty(),
        descricao = description.orEmpty(),
        cor = color.orEmpty(),
        nova = isNew,
        bloqueada = blocked,
        imagem = image?.url,
        citacao = (quote as? JsonObject)?.let { q ->
            q["quote"]?.jsonPrimitive?.contentOrNull?.let { CitacaoEra(it, q["author"]?.jsonPrimitive?.contentOrNull.orEmpty()) }
        },
    )
}
