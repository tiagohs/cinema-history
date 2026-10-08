package com.tiagohs.entities.tmdb.person

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.tmdb.*
import com.tiagohs.entities.tmdb.movie.Movie
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

    /** Biografia no idioma do app ([languageTag] como "pt-BR"), depois inglês, depois a primeira disponível. */
    fun setupPersonSummmary(languageTag: String = "pt-BR") {
        val translations = translations?.translations ?: emptyList()
        val language = languageTag.substringBefore('-')
        val country = languageTag.substringAfter('-', "")

        val preferred = translations.find { it.iso_639_1 == language && it.iso_3166_1 == country }?.data?.overview
            ?: translations.find { it.iso_639_1 == language && !it.data?.overview.isNullOrBlank() }?.data?.overview
        if (!preferred.isNullOrBlank()) {
            biography = preferred
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