package com.tiagohs.entities.omdb

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.RatingType

data class Rating (
	@SerializedName("Source") val source : RatingType,
	@SerializedName("Value") val value : String
)