package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieExtraInfo (

	@SerializedName("id") val id : Int,
	@SerializedName("review_results") val reviewResults : List<ReviewsResult>? = null
): Serializable