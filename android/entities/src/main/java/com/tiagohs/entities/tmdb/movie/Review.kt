package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.ReviewerEnum
import java.io.Serializable

class Review (
    @SerializedName("reviewer") val reviewer : ReviewerEnum? = null,
    @SerializedName("reviewer_site_name")  val reviewerSiteName : String? = null,
    @SerializedName("date_formated")  val dateFormated : String? = null,
    @SerializedName("reviewer_name")  val reviewerName : String,
    @SerializedName("review_url")  val reviewUrl : String,
    @SerializedName("review_description")  val reviewDescription : String,
    @SerializedName("review_rating")  val reviewRating : Float
): Serializable