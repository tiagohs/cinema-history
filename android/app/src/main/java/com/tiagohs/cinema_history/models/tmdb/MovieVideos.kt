package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName


data class MovieVideos (
	@SerializedName("results") val videoList : List<Video>
)