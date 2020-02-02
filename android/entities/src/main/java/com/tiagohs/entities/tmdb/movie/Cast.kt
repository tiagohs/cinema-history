package com.tiagohs.entities.tmdb.movie


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Cast (

	@SerializedName("cast_id") val castId : Int? = null,
	@SerializedName("character") val character : String? = null,
	@SerializedName("credit_id") val creditId : String? = null,
	@SerializedName("gender") val gender : Int? = null,
	@SerializedName("id") val id : Int? = null,
	@SerializedName("name") val name : String? = null,
	@SerializedName("order") val order : Int? = null,
	@SerializedName("profile_path") val profilePath : String? = null
): Serializable