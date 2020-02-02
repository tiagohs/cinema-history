package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.extensions.styledString
import kotlinx.android.synthetic.main.adapter_person_info_special_profile.view.*
import kotlinx.android.synthetic.main.view_person_profile_item.view.*

class PersonInfoSpecialProfileViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(person: Person) {
        val context = context ?: return
        val personProfile = person.extraInfo?.profile ?: return

        personProfile.forEach {
            val view = LayoutInflater.from(context).inflate(R.layout.view_person_profile_item, null, false).apply {
                layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT)
            }

            view.profileContent.setupLinkableTextView(context)

            view.profileItemTitle.text = it.years
            view.profileContent.text = it.content.styledString()

            itemView.personProfileContainer.addView(view)
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_special_profile
    }
}