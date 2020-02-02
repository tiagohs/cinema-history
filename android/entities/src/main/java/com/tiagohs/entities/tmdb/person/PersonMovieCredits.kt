package com.tiagohs.entities.tmdb.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PersonMovieCredits (

	@SerializedName("cast") val castCredits : List<PersonCastCredits>? = null,
	@SerializedName("crew") val crewCredits : List<PersonCrewCredits>? = null
): Serializable