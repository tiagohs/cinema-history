package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.tmdb.Image
import java.io.Serializable

data class TaggedImages (

	@SerializedName("results") val results : List<Image>,
	@SerializedName("page") val page : Int,
	@SerializedName("total_results") val totalResults : Int,
	@SerializedName("id") val id : Int,
	@SerializedName("total_pages") val totalPages : Int
): Serializable