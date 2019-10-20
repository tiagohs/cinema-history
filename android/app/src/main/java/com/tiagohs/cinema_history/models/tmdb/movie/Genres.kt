package com.tiagohs.cinema_history.models.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Genres (

	@SerializedName("id") val id : Int? = null,
	@SerializedName("name") val name : String? = null
): Serializable