package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName


data class SimilarMovies (

	@SerializedName("page") val page : Int,
	@SerializedName("results") val results : List<Video>,
	@SerializedName("total_pages") val totalPages : Int,
	@SerializedName("total_results") val totalResults : Int
)