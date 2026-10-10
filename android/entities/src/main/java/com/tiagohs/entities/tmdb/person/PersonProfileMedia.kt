package com.tiagohs.entities.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.Image
import java.io.Serializable

/**
 * Mídia intercalada no perfil de um diretor (specials/persons.json → profile[].media).
 *
 *  - type "image": [image] + [title] + [text] (legenda) + [source]
 *  - type "video": [videoId] (YouTube) + [title] + [text] + [source]
 *  - type "quote": [quote] + [author]
 */
data class PersonProfileMedia(
    @SerializedName("type") val type: String? = null,
    @SerializedName("image") val image: Image? = null,
    @SerializedName("video_id") val videoId: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("quote") val quote: String? = null,
    @SerializedName("author") val author: String? = null
) : Serializable {

    val isImage: Boolean get() = type == TYPE_IMAGE && image != null
    val isVideo: Boolean get() = type == TYPE_VIDEO && !videoId.isNullOrBlank()
    val isQuote: Boolean get() = type == TYPE_QUOTE && !quote.isNullOrBlank()

    companion object {
        const val TYPE_IMAGE = "image"
        const val TYPE_VIDEO = "video"
        const val TYPE_QUOTE = "quote"
    }
}
