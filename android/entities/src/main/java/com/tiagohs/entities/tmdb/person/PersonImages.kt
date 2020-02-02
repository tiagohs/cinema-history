package com.tiagohs.entities.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.tmdb.Image
import java.io.Serializable

data class PersonImages (
	@SerializedName("profiles") val profiles : List<Image>? = null
): Serializable