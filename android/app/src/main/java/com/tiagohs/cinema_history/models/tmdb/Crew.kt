package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Crew (

	@SerializedName("credit_id") val creditId : String,
	@SerializedName("department") val department : String,
	@SerializedName("gender") val gender : Int,
	@SerializedName("id") val id : Int,
	@SerializedName("job") val job : String,
	@SerializedName("name") val name : String,
	@SerializedName("profile_path") val profilePath : String
): Serializable