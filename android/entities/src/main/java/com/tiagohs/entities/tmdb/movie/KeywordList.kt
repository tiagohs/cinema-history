package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class KeywordList (
	@SerializedName("keywords") val list : List<Keyword>? = null
): Serializable