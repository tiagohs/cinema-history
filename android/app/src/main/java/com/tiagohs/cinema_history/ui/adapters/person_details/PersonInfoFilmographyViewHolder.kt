package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import com.tiagohs.cinema_history.ui.adapters.MovieItemAdapter
import com.tiagohs.entities.dto.MovieFilmographyDTO
import kotlinx.android.synthetic.main.adapter_movie_info_person_list.view.*


class PersonInfoFilmographyViewHolder(
    val context: Context?,
    view: View,
    private val onMovieSelected: ((movieId: Int) -> Unit)? = null
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(listTitle: String, movieList: List<MovieFilmographyDTO>) {
        val adapter = MovieItemAdapter(context, movieList)
        adapter.onMovieClicked = onMovieSelected

        itemView.personList.adapter = adapter
        itemView.personList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.personList.addItemDecoration(SpaceOffsetDecoration(6.convertIntToDp(context), SpaceOffsetDecoration.LEFT))

        itemView.personTitle.text = listTitle
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_person_list
    }
}