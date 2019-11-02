package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TranslationPersonData(
    @SerializedName("biography") val overview : String? = null
): Serializable