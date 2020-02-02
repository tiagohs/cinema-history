package com.tiagohs.entities.tmdb


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TranslationsResult<T> (

	@SerializedName("translations") val translations : List<Translation<T>>
): Serializable