package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.dto.MovieFilmographyDTO
import com.tiagohs.cinema_history.models.tmdb.ExternalIds

data class Person (

    @SerializedName("birthday") val birthday : String? = null,
    @SerializedName("known_for_department") val knownForDepartment : String? = null,
    @SerializedName("id") val id : Int? = null,
    @SerializedName("place_of_birth") val placeOfBirth : String? = null,
    @SerializedName("homepage") val homepage : String? = null,
    @SerializedName("profile_path") val profilePath : String? = null,
    @SerializedName("imdb_id") val imdbId : String? = null,
    @SerializedName("deathday") val deathday : String? = null,
    @SerializedName("images") val images : PersonImages? = null,
    @SerializedName("external_ids") val externalIds : ExternalIds? = null,
    @SerializedName("name") val name : String? = null,
    @SerializedName("also_known_as") val alsoKnownAs : List<String>? = null,
    @SerializedName("biography") val biography : String? = null,
    @SerializedName("movie_credits") val movieCredits : PersonMovieCredits? = null,
    @SerializedName("adult") val adult : Boolean? = null,
    @SerializedName("gender") val gender : Int? = null,
    @SerializedName("popularity") val popularity : Double? = null,
    @SerializedName("tagged_images") val taggedImages : TaggedImages? = null
) {

	var personFilmography: List<MovieFilmographyDTO> = emptyList()
	var departmentsList: List<String> = emptyList()

	fun generatePersonFilmography(): List<MovieFilmographyDTO> {
		val castMovies = movieCredits?.castCredits?.map {
            MovieFilmographyDTO(
                it.id,
                it.title ?: it.originalTitle,
                it.poster_path,
                character = it.character
            )
        } ?: emptyList()
		val crewMovies = movieCredits?.crewCredits?.groupBy { it.id }?.entries?.map { moviesGrouped ->
            MovieFilmographyDTO(
                moviesGrouped.value.firstOrNull()?.id,
                moviesGrouped.value.firstOrNull()?.title
                    ?: moviesGrouped.value.firstOrNull()?.originalTitle,
                moviesGrouped.value.firstOrNull()?.posterPath,
                departments = moviesGrouped.value.mapNotNull { it.department }.joinToString(", ")
            )
		} ?: emptyList()
		val allMovies = listOf(castMovies, crewMovies).flatten()

		return allMovies.groupBy { it.id }.entries.map { moviesGrouped ->
            MovieFilmographyDTO(
                moviesGrouped.value.firstOrNull()?.id,
                moviesGrouped.value.firstOrNull()?.title,
                moviesGrouped.value.firstOrNull()?.posterPath,
                moviesGrouped.value.mapNotNull { it.character }.joinToString(", "),
                moviesGrouped.value.mapNotNull { it.departments }.joinToString(", ")
            )
		}
	}

	fun generatePersonDepartmentsList(): List<String> {
		val departmentList = ArrayList<String>()

		if (!movieCredits?.castCredits.isNullOrEmpty()) {
			departmentList.add("Acting")
		}

		for (crewItem in movieCredits?.crewCredits ?: emptyList()) {
			val department = crewItem.department ?: continue

			if (departmentList.find { it == department } == null) {
				departmentList.add(department)
			}
		}

		return departmentList
	}
}