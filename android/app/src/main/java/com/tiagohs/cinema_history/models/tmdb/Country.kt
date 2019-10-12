package com.tiagohs.cinema_history.models.tmdb


import com.google.gson.annotations.SerializedName


data class Country (

	@SerializedName("certification") val certification : Int,
	@SerializedName("iso_3166_1") val iso3166_1 : String,
	@SerializedName("primary") val primary : Boolean,
	@SerializedName("release_date") val releaseDate : String
)