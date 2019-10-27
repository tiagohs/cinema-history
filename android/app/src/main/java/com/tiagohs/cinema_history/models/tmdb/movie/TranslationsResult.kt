package com.tiagohs.cinema_history.models.tmdb.movie


import com.google.gson.annotations.SerializedName

data class TranslationsResult (

	@SerializedName("translations") val translations : List<Translation>
)