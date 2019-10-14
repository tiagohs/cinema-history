package com.tiagohs.cinema_history.helpers.utils

import android.content.Context
import android.icu.text.NumberFormat
import com.tiagohs.cinema_history.R

import java.util.ArrayList
import java.util.Calendar
import java.util.Collections
import java.util.Comparator
import java.util.GregorianCalendar
import java.util.Locale

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


    fun formatCurrency(currency: Long): String {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            return format.format(currency)
        } else {
            val formatter = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
            return formatter.format(currency)
        }
    }

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
        val ids = ids ?: return emptyList()
        val context = context ?: return emptyList()

        var genres = arrayListOf<String>()
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

}