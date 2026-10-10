package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.ReviewerEnum
import java.io.Serializable

class Review (
    @SerializedName("reviewer") val reviewer : ReviewerEnum? = null,
    @SerializedName("reviewer_site_name")  val reviewerSiteName : String? = null,
    /** Data já formatada (legado, em português). Prefira [date]. */
    @SerializedName("date_formated")  val dateFormated : String? = null,
    /** Data da crítica no formato ISO (yyyy-MM-dd), formatada no idioma do app. */
    @SerializedName("date")  val date : String? = null,
    @SerializedName("reviewer_name")  val reviewerName : String? = null,
    @SerializedName("review_url")  val reviewUrl : String,
    @SerializedName("review_description")  val reviewDescription : String,
    /** Nota de 0 a 5; ausente quando o veículo não dá nota. */
    @SerializedName("review_rating")  val reviewRating : Float? = null
): Serializable
