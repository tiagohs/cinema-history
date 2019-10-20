package com.tiagohs.cinema_history.models.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Video (

	@SerializedName("id") val id : String? = null,
	@SerializedName("iso_639_1") val iso_639_1 : String? = null,
	@SerializedName("iso_3166_1") val iso_3166_1 : String? = null,
	@SerializedName("key") val key : String? = null,
	@SerializedName("name") val name : String? = null,
	@SerializedName("site") val site : String? = null,
	@SerializedName("size") val size : Int? = null,
	@SerializedName("type") val type : String? = null
): Serializable