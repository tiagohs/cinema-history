package com.tiagohs.cinema_history.presentation.adapters.person_details

import com.tiagohs.cinema_history.databinding.AdapterMovieInfoPersonListBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.presentation.adapters.MovieItemAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.person_info.PersonInfoMovieList
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration


class PersonInfoFilmographyViewHolder(
    private val binding: AdapterMovieInfoPersonListBinding,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
) : BaseViewHolder<PersonInfo>(binding) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val personInfoMovieList = item as? PersonInfoMovieList ?: return
        val context = itemView.context ?: return
        val listTitle = personInfoMovieList.listTitle
        val movieList = personInfoMovieList.movieList

        binding.personList.apply {
            adapter = MovieItemAdapter(movieList).apply {
                onMovieClicked = onMovieSelected
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(
                SpaceOffsetDecoration(
                    6.convertIntToDp(context),
                    SpaceOffsetDecoration.LEFT
                )
            )
        }

        binding.personTitle.setResourceText(listTitle)
    }
}