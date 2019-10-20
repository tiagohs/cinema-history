package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName

data class ExternalIds (

	@SerializedName("freebase_id") val freebaseId : String,
	@SerializedName("instagram_id") val instagramId : String,
	@SerializedName("tvrage_id") val tvrageId : Int,
	@SerializedName("twitter_id") val twitterId : String,
	@SerializedName("freebase_mid") val freebaseMid : String,
	@SerializedName("imdb_id") val imdbId : String,
	@SerializedName("facebook_id") val facebookId : String
)