package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Credits (
    @SerializedName("cast") val cast : List<Cast>? = null,
    @SerializedName("crew") val crew : List<Crew>? = null
): Serializable