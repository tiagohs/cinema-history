package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SimilarMovies (
    @SerializedName("page") val page : Int? = null,
    @SerializedName("results") val results : List<Video>? = null,
    @SerializedName("total_pages") val totalPages : Int? = null,
    @SerializedName("total_results") val totalResults : Int? = null
): Serializable