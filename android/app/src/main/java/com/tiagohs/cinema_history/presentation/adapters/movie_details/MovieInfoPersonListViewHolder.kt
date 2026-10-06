package com.tiagohs.cinema_history.presentation.adapters.movie_details

import com.tiagohs.cinema_history.databinding.AdapterMovieInfoPersonListBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.presentation.adapters.PersonAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.movie_info.MovieInfoPersonList
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration


class MovieInfoPersonListViewHolder(
    private val binding: AdapterMovieInfoPersonListBinding,
    private val onPersonClicked: ((personId: Int) -> Unit)?
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val movieInfo = item as? MovieInfoPersonList ?: return

        val listTitle = movieInfo.listTitle
        val persons = movieInfo.personList

        if (persons.isEmpty()) {
            binding.personListContainer.hide()
            return
        }

        binding.personTitle.setResourceText(listTitle)
        binding.personList.apply {
            adapter = PersonAdapter(persons, isSpecial = false, onPersonClicked)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(
                SpaceOffsetDecoration(
                    8.convertIntToDp(context),
                    SpaceOffsetDecoration.LEFT
                )
            )
        }

    }
}