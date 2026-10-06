package com.tiagohs.cinema_history.presentation.adapters.person_details

import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialFilmographyBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.extensions.setupParallaxScrollListener
import com.tiagohs.cinema_history.presentation.adapters.MovieItemSpecialAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import cz.intik.overflowindicator.SimpleSnapHelper

class PersonInfoSpecialFilmographyViewHolder(
    private val binding: AdapterPersonInfoSpecialFilmographyBinding,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
) : BaseViewHolder<PersonInfo>(binding) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val person = item.person
        val filmography = person.personFilmography
        val personAdapter = MovieItemSpecialAdapter(filmography)

        personAdapter.onMovieClicked = onMovieSelected

        binding.filmographyList.apply {
            adapter = personAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            binding.filmographyListIndicator.attachToRecyclerView(this)
            SimpleSnapHelper(binding.filmographyListIndicator).attachToRecyclerView(this)

            setupParallaxScrollListener()
        }
    }
}