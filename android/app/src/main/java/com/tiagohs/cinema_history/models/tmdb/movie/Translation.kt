package com.tiagohs.cinema_history.models.tmdb.movie


import com.google.gson.annotations.SerializedName

data class Translation (

	@SerializedName("iso_3166_1") val iso_3166_1 : String? = null,
	@SerializedName("iso_639_1") val iso_639_1 : String? = null
)