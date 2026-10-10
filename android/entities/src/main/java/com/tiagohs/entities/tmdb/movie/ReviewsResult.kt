package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.R
import java.io.Serializable

/**
 * Críticas agrupadas pelo idioma em que foram escritas: "pt", "en" ou "es"
 * (aceita também as tags antigas "pt-BR" e "en-US").
 */
class ReviewsResult (
    @SerializedName("language") val languageISO : String? = null,
    @SerializedName("reviews")  val reviews : List<Review>? = null
): Serializable {

    /** Código do idioma (pt, en, es). */
    val languageCode: String
        get() = languageISO?.substringBefore('-')?.lowercase() ?: "en"

    /** Nome do idioma para exibir ("Português", "Inglês", "Espanhol"). */
    val languageNameRes: Int
        get() = when (languageCode) {
            "pt" -> R.string.review_language_portuguese
            "es" -> R.string.review_language_spanish
            else -> R.string.review_language_english
        }
}
