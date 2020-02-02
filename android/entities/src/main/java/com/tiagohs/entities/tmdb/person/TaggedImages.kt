package com.tiagohs.entities.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.tmdb.Image
import java.io.Serializable

data class TaggedImages (

	@SerializedName("results") val results : List<Image>,
	@SerializedName("page") val page : Int,
	@SerializedName("total_results") val totalResults : Int,
	@SerializedName("id") val id : Int,
	@SerializedName("total_pages") val totalPages : Int
): Serializable