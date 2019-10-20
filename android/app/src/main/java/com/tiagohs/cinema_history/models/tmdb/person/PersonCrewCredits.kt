package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName

data class PersonCrewCredits (

	@SerializedName("department") val department : String,
	@SerializedName("job") val job : String,
	@SerializedName("credit_id") val creditId : String,

	@SerializedName("id") val id : Int,
	@SerializedName("original_language") val originalLanguage : String,
	@SerializedName("original_title") val originalTitle : String,
	@SerializedName("overview") val overview : String,
	@SerializedName("genre_ids") val genreIds : List<Int>,
	@SerializedName("video") val video : Boolean,
	@SerializedName("release_date") val releaseDate : String,
	@SerializedName("popularity") val popularity : Double,
	@SerializedName("vote_average") val voteAverage : Double,
	@SerializedName("vote_count") val voteCount : Int,
	@SerializedName("title") val title : String,
	@SerializedName("adult") val adult : Boolean,
	@SerializedName("backdrop_path") val backdropPath : String,
	@SerializedName("poster_path") val posterPath : String
)