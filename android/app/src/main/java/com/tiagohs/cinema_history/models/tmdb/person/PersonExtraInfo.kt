package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PersonExtraInfo (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("custom_name") val customName : String,
	@SerializedName("highlight_image") val highlight_image : String,
	@SerializedName("quote") val quote : String,
	@SerializedName("profile") val profile : List<PersonProfile>,
	@SerializedName("awards") val awards : String,
	@SerializedName("videos") val videos : List<PersonVideo>
): Serializable