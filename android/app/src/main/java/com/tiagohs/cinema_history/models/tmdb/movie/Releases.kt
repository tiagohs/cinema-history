package com.tiagohs.cinema_history.models.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Releases (
	@SerializedName("countries") val countries : List<Country>? = null
): Serializable