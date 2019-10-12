package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName


data class MovieImages (
	@SerializedName("backdrops") val backdrops : List<Image>,
	@SerializedName("posters") val posters : List<Image>
)