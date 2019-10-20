package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName

data class PersonMovieCredits (

	@SerializedName("castCredits") val castCredits : List<PersonCastCredits>,
	@SerializedName("crewCredits") val crewCredits : List<PersonCrewCredits>
)