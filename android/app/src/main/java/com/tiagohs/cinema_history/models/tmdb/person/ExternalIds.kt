package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName

data class ExternalIds (

	@SerializedName("freebase_id") val freebaseId : String? = null,
	@SerializedName("instagram_id") val instagramId : String? = null,
	@SerializedName("tvrage_id") val tvrageId : Int? = null,
	@SerializedName("twitter_id") val twitterId : String? = null,
	@SerializedName("freebase_mid") val freebaseMid : String? = null,
	@SerializedName("imdb_id") val imdbId : String? = null,
	@SerializedName("facebook_id") val facebookId : String? = null
)