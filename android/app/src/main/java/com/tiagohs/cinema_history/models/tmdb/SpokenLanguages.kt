package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SpokenLanguages (
	@SerializedName("iso_639_1") val iso_639_1 : String,
	@SerializedName("name") val name : String
): Serializable