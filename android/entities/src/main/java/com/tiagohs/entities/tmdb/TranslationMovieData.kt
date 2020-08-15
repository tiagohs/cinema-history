package com.tiagohs.entities.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TranslationMovieData(
    @SerializedName("title") val title : String? = null,
    @SerializedName("overview") val overview : String? = null,
    @SerializedName("homepage") val homepage : String? = null
): Serializable