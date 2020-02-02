package com.tiagohs.entities.tmdb.movie


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Country (

	@SerializedName("certification") val certification : String? = null,
	@SerializedName("iso_3166_1") val iso3166_1 : String? = null,
	@SerializedName("primary") val primary : Boolean? = null,
	@SerializedName("release_date") val releaseDate : String? = null
): Serializable