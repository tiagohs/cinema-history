package com.tiagohs.helpers.utils

import android.content.Context
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.helpers.R
import com.tiagohs.entities.dto.RatingDTO
import com.tiagohs.entities.tmdb.movie.Country
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.entities.tmdb.person.PersonMovieCredits

import java.util.Calendar
import java.util.GregorianCalendar

object MovieUtils {

    val allGenrerIDs: IntArray
        get() = intArrayOf(
            28,
            12,
            16,
            35,
            80,
            99,
            18,
            10751,
            14,
            36,
            27,
            10402,
            9648,
            10749,
            878,
            10770,
            53,
            10752,
            37
        )

    val allGenrerNames: IntArray
        get() = intArrayOf(
            R.string.genere_action,
            R.string.genere_adventure,
            R.string.genere_animation,
            R.string.genere_comedy,
            R.string.genere_crime,
            R.string.genere_documentary,
            R.string.genere_drama,
            R.string.genere_family,
            R.string.genere_fantasy,
            R.string.genere_history,
            R.string.genere_horror,
            R.string.genere_music,
            R.string.genere_mystery,
            R.string.genere_romance,
            R.string.genere_science_fiction,
            R.string.genere_tv_movie,
            R.string.genere_thriller,
            R.string.genere_war,
            R.string.genere_western
        )

    fun getAge(_year: Int, _month: Int, _day: Int): Int {

        val cal = GregorianCalendar()
        val y: Int
        val m: Int
        val d: Int
        var a: Int

        y = cal.get(Calendar.YEAR)
        m = cal.get(Calendar.MONTH)
        d = cal.get(Calendar.DAY_OF_MONTH)
        cal.set(_year, _month, _day)
        a = y - cal.get(Calendar.YEAR)
        if (m < cal.get(Calendar.MONTH) || m == cal.get(Calendar.MONTH) && d < cal
                .get(Calendar.DAY_OF_MONTH)
        ) {
            --a
        }
        require(a >= 0) { "Age < 0" }
        return a
    }

    fun setupBirthdayInfo(person: Person?) {
        person ?: return

        val birthdayDate = person.birthday
        val placeOfBirth = person.placeOfBirth

        if (birthdayDate.isNullOrEmpty() && placeOfBirth.isNullOrEmpty()) { return }

        if (placeOfBirth.isNullOrEmpty() && !birthdayDate.isNullOrEmpty()) {
            person.birthdayFormated = DateUtils.formateDate(birthdayDate, "MMMM dd, yyyy")
            return
        }

        if (!placeOfBirth.isNullOrEmpty() && birthdayDate.isNullOrEmpty()) {
            person.birthdayFormated = placeOfBirth
            return
        }

        person.birthdayFormated = "${DateUtils.formateDate(birthdayDate!!, "MMMM dd, yyyy")} in $placeOfBirth"
    }

    fun generatePersonMovieCredits(movieCredits : PersonMovieCredits?): List<MovieFilmographyDTO> {
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

        return allMovies.groupBy { it.id }.entries.map { moviesGrouped ->
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

    fun getAge(calendar: Calendar): Int {
        return getAge(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun formatGenres(context: Context, ids: List<Int>?): String {

        if (ids == null || ids.size < 0)
            return ""

        var genre = ""
        var name = ""
        for (i in ids.indices) {
            val id = ids[i]
            when (id) {

                28 -> name = context.getString(R.string.genere_action)
                12 -> name = context.getString(R.string.genere_adventure)
                16 -> name = context.getString(R.string.genere_animation)
                35 -> name = context.getString(R.string.genere_comedy)
                80 -> name = context.getString(R.string.genere_crime)
                99 -> name = context.getString(R.string.genere_documentary)
                18 -> name = context.getString(R.string.genere_drama)
                10751 -> name = context.getString(R.string.genere_family)
                14 -> name = context.getString(R.string.genere_fantasy)
                36 -> name = context.getString(R.string.genere_history)
                27 -> name = context.getString(R.string.genere_horror)
                10402 -> name = context.getString(R.string.genere_music)
                9648 -> name = context.getString(R.string.genere_mystery)
                10749 -> name = context.getString(R.string.genere_romance)
                878 -> name = context.getString(R.string.genere_science_fiction)
                10770 -> name = context.getString(R.string.genere_tv_movie)
                53 -> name = context.getString(R.string.genere_thriller)
                10752 -> name = context.getString(R.string.genere_war)
                37 -> name = context.getString(R.string.genere_western)
            }
            genre = "$genre, $name"
        }
        return genre.substring(if (genre.length > 0) 1 else 0)
    }

    fun getGenresName(context: Context?, ids: List<Int>?): List<String> {
        ids ?: return emptyList()
        context ?: return emptyList()

        val genres = arrayListOf<String>()
        var name = ""

        for (i in ids.indices) {
            val id = ids[i]
            when (id) {

                28 -> name = context.getString(R.string.genere_action)
                12 -> name = context.getString(R.string.genere_adventure)
                16 -> name = context.getString(R.string.genere_animation)
                35 -> name = context.getString(R.string.genere_comedy)
                80 -> name = context.getString(R.string.genere_crime)
                99 -> name = context.getString(R.string.genere_documentary)
                18 -> name = context.getString(R.string.genere_drama)
                10751 -> name = context.getString(R.string.genere_family)
                14 -> name = context.getString(R.string.genere_fantasy)
                36 -> name = context.getString(R.string.genere_history)
                27 -> name = context.getString(R.string.genere_horror)
                10402 -> name = context.getString(R.string.genere_music)
                9648 -> name = context.getString(R.string.genere_mystery)
                10749 -> name = context.getString(R.string.genere_romance)
                878 -> name = context.getString(R.string.genere_science_fiction)
                10770 -> name = context.getString(R.string.genere_tv_movie)
                53 -> name = context.getString(R.string.genere_thriller)
                10752 -> name = context.getString(R.string.genere_war)
                37 -> name = context.getString(R.string.genere_western)
            }
            genres.add(name)
        }

        return genres
    }

    fun getRating(countries: List<Country>?): RatingDTO? {
        val countryList = countries ?: return null

        val certificationBrazil = countryList.find { it.iso3166_1 == "BR" }
        if (certificationBrazil != null && !certificationBrazil.certification.isNullOrEmpty()) {
            return RatingDTO(
                "BR",
                certificationBrazil.certification ?: "",
                R.color.md_white_1000,
                getBrazilRatingColor(certificationBrazil.certification ?: "")
            )
        }

        val certificationEua = countryList.find { it.iso3166_1 == "US" }
        if (certificationEua != null && !certificationEua.certification.isNullOrEmpty()) {
            return com.tiagohs.entities.dto.RatingDTO(
                "US",
                certificationEua.certification ?: "",
                R.color.md_white_1000,
                R.color.md_black_1000
            )
        }

        return null
    }

    fun getBrazilRatingColor(certificate: String): Int {
        return when(certificate) {
            "L" -> R.color.rating_br_livre
            "10" -> R.color.rating_br_10
            "12" -> R.color.rating_br_12
            "14" -> R.color.rating_br_14
            "16" -> R.color.rating_br_16
            "18" -> R.color.rating_br_18
            else -> R.color.rating_br_unknown
        }

    }

}
