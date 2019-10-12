package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName


data class KeywordList (

	@SerializedName("keywords") val list : List<Keyword>
)