package com.tiagohs.cinema_history.models.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MovieVideos (
	@SerializedName("results") val videoList : List<Video>? = null
): Serializable