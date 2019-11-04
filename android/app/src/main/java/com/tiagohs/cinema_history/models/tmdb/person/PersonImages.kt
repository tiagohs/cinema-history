package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.tmdb.Image
import java.io.Serializable

data class PersonImages (
	@SerializedName("profiles") val profiles : List<Image>? = null
): Serializable