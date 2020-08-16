package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_person_info_biography.view.*

class PersonInfoBiographyViewHolder(
    view: View
) : BaseViewHolder<PersonInfo>(view) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)

        itemView.personBiography.setResourceText(item.person.biography)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_biography
    }
}