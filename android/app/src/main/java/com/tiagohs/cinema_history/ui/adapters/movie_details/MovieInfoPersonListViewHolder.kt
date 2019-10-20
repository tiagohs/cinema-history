package com.tiagohs.cinema_history.ui.adapters.movie_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.tools.SpaceOffsetDecoration
import com.tiagohs.cinema_history.models.PersonDTO
import com.tiagohs.cinema_history.ui.adapters.PersonAdapter
import kotlinx.android.synthetic.main.adapter_movie_info_person_list.view.*


class MovieInfoPersonListViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(listTitle: String, personList: List<PersonDTO>) {
        val adapter = PersonAdapter(context, personList)

        itemView.personList.adapter = adapter
        itemView.personList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.personList.addItemDecoration(SpaceOffsetDecoration(6.convertIntToDp(context), SpaceOffsetDecoration.LEFT))

        itemView.personTitle.text = listTitle
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_person_list
    }
}