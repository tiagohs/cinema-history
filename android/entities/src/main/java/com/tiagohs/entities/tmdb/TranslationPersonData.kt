package com.tiagohs.entities.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TranslationPersonData(
    @SerializedName("biography") val overview : String? = null
): Serializable