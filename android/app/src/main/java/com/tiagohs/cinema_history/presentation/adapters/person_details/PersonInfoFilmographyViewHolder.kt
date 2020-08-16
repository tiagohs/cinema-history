package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieItemAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.person_info.PersonInfoMovieList
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.adapter_movie_info_person_list.view.*


class PersonInfoFilmographyViewHolder(
    view: View,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
) : BaseViewHolder<PersonInfo>(view) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val personInfoMovieList = item as? PersonInfoMovieList ?: return
        val context = containerView.context ?: return
        val listTitle = personInfoMovieList.listTitle
        val movieList = personInfoMovieList.movieList

        itemView.personList.apply {
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

        itemView.personTitle.setResourceText(listTitle)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_person_list
    }
}