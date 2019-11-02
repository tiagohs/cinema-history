package com.tiagohs.cinema_history.models.tmdb


import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.tmdb.TranslationMovieData
import java.io.Serializable

data class Translation<T> (

	@SerializedName("iso_3166_1") val iso_3166_1 : String? = null,
	@SerializedName("iso_639_1") val iso_639_1 : String? = null,
	@SerializedName("name") val name : String? = null,
	@SerializedName("english_name") val englishName : String? = null,
	@SerializedName("data") val data : T? = null
): Serializable