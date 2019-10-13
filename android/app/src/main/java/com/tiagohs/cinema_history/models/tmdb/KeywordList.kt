package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class KeywordList (
	@SerializedName("keywords") val list : List<Keyword>
): Serializable