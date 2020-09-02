package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ReviewsResult (
    @SerializedName("language") val languageISO : String? = null,
    @SerializedName("reviews")  val reviews : List<Review>? = null
): Serializable