package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName


data class Releases (

	@SerializedName("countries") val countries : List<Country>
)