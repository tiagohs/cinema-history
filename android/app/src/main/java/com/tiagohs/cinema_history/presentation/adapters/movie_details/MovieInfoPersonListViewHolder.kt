package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.PersonAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.movie_info.MovieInfoPersonList
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.adapter_movie_info_person_list.*
import kotlinx.android.synthetic.main.adapter_movie_info_person_list.view.*


class MovieInfoPersonListViewHolder(
    view: View,
    private val onPersonClicked: ((personId: Int) -> Unit)?
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val movieInfo = item as? MovieInfoPersonList ?: return

        val listTitle = movieInfo.listTitle
        val persons = movieInfo.personList

        if (persons.isEmpty()) {
            personListContainer.hide()
            return
        }

        personTitle.setResourceText(listTitle)
        personList.apply {
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

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_person_list
    }
}