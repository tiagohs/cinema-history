package com.tiagohs.cinema_history.models.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.models.dto.MovieFilmographyDTO
import com.tiagohs.cinema_history.models.tmdb.*
import java.io.Serializable

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
    @SerializedName("biography") var biography : String? = null,
    @SerializedName("movie_credits") val movieCredits : PersonMovieCredits? = null,
    @SerializedName("adult") val adult : Boolean? = null,
    @SerializedName("gender") val gender : Int? = null,
    @SerializedName("popularity") val popularity : Double? = null,
    @SerializedName("tagged_images") val taggedImages : TaggedImages? = null,
    @SerializedName("translations") val translations : TranslationsResult<TranslationPersonData>? = null
): Serializable {

	var personFilmography: List<MovieFilmographyDTO> = emptyList()
	var departmentsList: List<String> = emptyList()
    var extraInfo: PersonExtraInfo? = null
    var allImages: ArrayList<Image> = ArrayList()
    var birthdayFormated: String = ""

    fun setupBirthdayInfo() {
        val birthdayDate = birthday
        val placeOfBirth = placeOfBirth

        if (birthdayDate.isNullOrEmpty() && placeOfBirth.isNullOrEmpty()) { return }

        if (placeOfBirth.isNullOrEmpty() && !birthdayDate.isNullOrEmpty()) {
            birthdayFormated = DateUtils.formateDate(birthdayDate, "MMMM dd, yyyy")
        }

        if (!placeOfBirth.isNullOrEmpty() && birthdayDate.isNullOrEmpty()) {
            birthdayFormated = placeOfBirth
        }

        birthdayFormated = "${DateUtils.formateDate(birthdayDate!!, "MMMM dd, yyyy")} in $placeOfBirth"
    }

	fun generatePersonFilmography() {
		val castMovies = movieCredits?.castCredits?.map {
            MovieFilmographyDTO(
                it.id,
                it.title ?: it.originalTitle,
                it.poster_path,
                it.backdrop_path,
                it.releaseDate,
                it.overview,
                character = it.character,
                year = if (it.releaseDate != null) DateUtils.getYearByDate(it.releaseDate) else null
            )
        } ?: emptyList()
		val crewMovies = movieCredits?.crewCredits?.groupBy { it.id }?.entries?.map { moviesGrouped ->
            MovieFilmographyDTO(
                moviesGrouped.value.firstOrNull()?.id,
                moviesGrouped.value.firstOrNull()?.title
                    ?: moviesGrouped.value.firstOrNull()?.originalTitle,
                moviesGrouped.value.firstOrNull()?.posterPath,
                moviesGrouped.value.firstOrNull()?.backdropPath,
                moviesGrouped.value.firstOrNull()?.releaseDate,
                moviesGrouped.value.firstOrNull()?.overview,
                departments = moviesGrouped.value.mapNotNull { it.department }.joinToString(", "),
                year = if (moviesGrouped.value.firstOrNull()?.releaseDate != null) DateUtils.getYearByDate(moviesGrouped.value.firstOrNull()?.releaseDate) else null
            )
		} ?: emptyList()
		val allMovies = listOf(castMovies, crewMovies).flatten()

        personFilmography = allMovies.groupBy { it.id }.entries.map { moviesGrouped ->
            MovieFilmographyDTO(
                moviesGrouped.value.firstOrNull()?.id,
                moviesGrouped.value.firstOrNull()?.title,
                moviesGrouped.value.firstOrNull()?.posterPath,
                moviesGrouped.value.firstOrNull()?.backdrop,
                moviesGrouped.value.firstOrNull()?.releaseDate,
                moviesGrouped.value.firstOrNull()?.overview,
                moviesGrouped.value.mapNotNull { it.character }.joinToString(", "),
                moviesGrouped.value.mapNotNull { it.departments }.joinToString(", "),
                moviesGrouped.value.firstOrNull()?.year
            )
		}
            .sortedBy { it.year }
            .reversed()
	}

	fun generatePersonDepartmentsList() {
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

        departmentsList = departmentList
	}

    fun setupPersonImages() {
        allImages = ArrayList(images?.profiles ?: emptyList())
        allImages.addAll(taggedImages?.results ?: emptyList())
    }

    fun setupPersonSummmary() {
        val translations = translations?.translations ?: emptyList()

        val portugueseOverview = translations.find { it.iso_639_1 == "pt" && it.iso_3166_1 == "BR" }?.data?.overview
        if (!portugueseOverview.isNullOrBlank()) {
            biography = portugueseOverview
            return
        }

        val englishOverview = translations.find { it.iso_639_1 == "en" && it.iso_3166_1 == "US" }?.data?.overview
        if (!englishOverview.isNullOrBlank()) {
            biography = englishOverview
            return
        }

        val otherOverview = translations.firstOrNull()?.data?.overview
        if (!otherOverview.isNullOrBlank()) {
            biography = otherOverview
        }
    }
}