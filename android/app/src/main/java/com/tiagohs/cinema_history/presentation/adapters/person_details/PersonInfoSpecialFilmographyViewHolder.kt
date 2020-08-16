package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupParallaxScrollListener
import com.tiagohs.cinema_history.presentation.adapters.MovieItemSpecialAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import cz.intik.overflowindicator.SimpleSnapHelper
import kotlinx.android.synthetic.main.adapter_person_info_special_filmography.view.*

class PersonInfoSpecialFilmographyViewHolder(
    view: View,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
) : BaseViewHolder<PersonInfo>(view) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val person = item.person
        val filmography =
            person.personFilmography.filter { !it.posterPath.isNullOrBlank() && !it.backdrop.isNullOrBlank() }
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