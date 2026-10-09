package com.tiagohs.cinema_history.presentation.adapters.home

import android.content.Context
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.image.ImageStyle
import com.tiagohs.entities.main_topics.MainTopicItem

/** Utilidades compartilhadas pelos componentes da Home. */
object HomeImages {

    /**
     * Imagem de capa de uma era para cartões/destaque: usa a imagem da apresentação (sempre recortada)
     * e, sem ela, a imagem do card. Gera uma cópia sem o redimensionamento do JSON para não alterar
     * o objeto original (que também é usado pela lista de eras).
     */
    fun eraCover(era: MainTopicItem): Image {
        val source = era.presentationImage ?: era.image

        return Image(
            imageType = source.imageType,
            url = source.url,
            contentDescription = source.contentDescription,
            imageStyle = ImageStyle(scaleType = "center_crop")
        )
    }

    fun coverOf(image: Image): Image = Image(
        imageType = image.imageType,
        url = image.url,
        contentDescription = image.contentDescription,
        imageStyle = ImageStyle(scaleType = "center_crop")
    )

    /** Cor da era (nome de cor do JSON, ex.: "md_red_500"); devolve [fallback] se não existir. */
    fun colorByName(context: Context, colorName: String?, fallback: Int): Int {
        if (colorName.isNullOrBlank()) return fallback

        val id = context.resources.getIdentifier(colorName, "color", context.packageName)
        if (id == 0) return fallback

        return try {
            androidx.core.content.ContextCompat.getColor(context, id)
        } catch (ex: Exception) {
            fallback
        }
    }
}
