package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName

data class Person (

	@SerializedName("birthday") val birthday : String,
	@SerializedName("known_for_department") val knownForDepartment : String,
	@SerializedName("id") val id : Int,
	@SerializedName("place_of_birth") val placeOfBirth : String,
	@SerializedName("homepage") val homepage : String,
	@SerializedName("profile_path") val profilePath : String,
	@SerializedName("imdb_id") val imdbId : String,
	@SerializedName("deathday") val deathday : String,
	@SerializedName("images") val images : PersonImages,
	@SerializedName("external_ids") val externalIds : ExternalIds,
	@SerializedName("name") val name : String,
	@SerializedName("also_known_as") val alsoKnownAs : List<String>,
	@SerializedName("biography") val biography : String,
	@SerializedName("movie_credits") val movieCredits : PersonMovieCredits,
	@SerializedName("adult") val adult : Boolean,
	@SerializedName("gender") val gender : Int,
	@SerializedName("popularity") val popularity : Double,
	@SerializedName("tagged_images") val taggedImages : TaggedImages
)