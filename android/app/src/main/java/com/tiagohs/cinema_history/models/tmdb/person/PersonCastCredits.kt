package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName

data class PersonCastCredits (

	@SerializedName("poster_path") val poster_path : String,
	@SerializedName("adult") val adult : Boolean,
	@SerializedName("backdrop_path") val backdrop_path : String,
	@SerializedName("vote_count") val vote_count : Int,
	@SerializedName("video") val video : Boolean,
	@SerializedName("id") val id : Int,
	@SerializedName("popularity") val popularity : Double,
	@SerializedName("genre_ids") val genre_ids : List<Int>,
	@SerializedName("original_language") val originalLanguage : String,
	@SerializedName("title") val title : String,
	@SerializedName("original_title") val originalTitle : String,
	@SerializedName("release_date") val releaseDate : String,
	@SerializedName("character") val character : String,
	@SerializedName("vote_average") val voteAverage : Double,
	@SerializedName("overview") val overview : String,
	@SerializedName("credit_id") val creditId : String
)