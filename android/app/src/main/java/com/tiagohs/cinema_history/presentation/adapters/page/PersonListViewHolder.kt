package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.PersonAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentPersonList
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.movie_info.MovieInfoPersonList
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.adapter_page_person_list.*

class PersonListViewHolder(
    view: View,
    private val onPersonClicked: ((personId: Int) -> Unit)?
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentPersonList = item as? ContentPersonList ?: return

        val persons = contentPersonList.persons?.map {
            PersonDTO(
                it.id,
                it.profilePath,
                it.name
            )
        } ?: emptyList()

        personList.apply {
            adapter = PersonAdapter(persons, onPersonClicked)
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
        const val LAYOUT_ID = R.layout.adapter_page_person_list
    }
}