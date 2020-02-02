package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.tmdb.person.Person
import kotlinx.android.synthetic.main.adapter_person_info_biography.view.*

class PersonInfoBiographyViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(person: Person) {
        itemView.personBiography.text = person.biography
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_biography
    }
}