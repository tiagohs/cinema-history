package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Crew (

	@SerializedName("credit_id") val creditId : String? = null,
	@SerializedName("department") val department : String? = null,
	@SerializedName("gender") val gender : Int? = null,
	@SerializedName("id") val id : Int? = null,
	@SerializedName("job") val job : String? = null,
	@SerializedName("name") val name : String? = null,
	@SerializedName("profile_path") val profilePath : String? = null
): Serializable