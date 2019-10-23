package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName

data class PersonMovieCredits (

	@SerializedName("cast") val castCredits : List<PersonCastCredits>? = null,
	@SerializedName("crew") val crewCredits : List<PersonCrewCredits>? = null
)