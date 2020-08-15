package com.tiagohs.entities.tmdb.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PersonProfile (

	@SerializedName("years") val years : String,
	@SerializedName("content") val content : String
): Serializable