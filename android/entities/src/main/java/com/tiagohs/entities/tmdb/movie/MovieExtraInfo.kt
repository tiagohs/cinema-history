package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.Quote
import com.tiagohs.entities.contents.ContentBlockSpecial
import com.tiagohs.entities.contents.ContentQuote
import java.io.Serializable

data class MovieExtraInfo (
	@SerializedName("id") val id : Int,
	@SerializedName("review_results") val reviewResults : List<ReviewsResult>? = null,
	@SerializedName("quote") val quote : ContentQuote? = null,
	@SerializedName("watchOn") val watchOn : List<Network>? = null,
	@SerializedName("did_you_know_list") val didYouKnowList : List<DidYouKnow>? = null,
	@SerializedName("block_special") val blockSpecial : ContentBlockSpecial? = null,
): Serializable