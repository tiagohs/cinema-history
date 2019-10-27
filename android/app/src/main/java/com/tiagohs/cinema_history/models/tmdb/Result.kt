package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result<T>(
    @SerializedName("id") val id : Int? = null,
    @SerializedName("results") val results : List<T>? = null
): Serializable