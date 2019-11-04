package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PersonVideo (

	@SerializedName("name") val name : String,
	@SerializedName("source") val source : String,
	@SerializedName("type") val type : String,
	@SerializedName("key") val key : String
): Serializable