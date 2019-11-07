package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.tmdb.person.Person
import com.tiagohs.cinema_history.ui.adapters.MovieItemSpecialAdapter
import com.tiagohs.cinema_history.ui.adapters.setupParallaxScrollListener
import cz.intik.overflowindicator.SimpleSnapHelper
import kotlinx.android.synthetic.main.adapter_person_info_special_filmography.view.*

class PersonInfoSpecialFilmographyViewHolder(
    val context: Context?,
    view: View,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(person: Person) {
        val filmography = person.personFilmography.filter { !it.posterPath.isNullOrBlank() && !it.backdrop.isNullOrBlank() }
        val personAdapter = MovieItemSpecialAdapter(context, filmography)

        personAdapter.onMovieClicked = onMovieSelected

        itemView.filmographyList.apply {
            adapter = personAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            itemView.filmographyListIndicator.attachToRecyclerView(this)
            SimpleSnapHelper(itemView.filmographyListIndicator).attachToRecyclerView(this)

            setupParallaxScrollListener()
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_special_filmography
    }
}