package com.tiagohs.entities.tmdb

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.tmdb.movie.MovieExtraInfo

data class MovieExtraInfoResult(
    @SerializedName("list") val list : String? = null,
    @SerializedName("movies") val movies : List<MovieExtraInfo>? = null
)