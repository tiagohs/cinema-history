package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_person_info_special_profile.view.*
import kotlinx.android.synthetic.main.view_person_profile_item.view.*

class PersonInfoSpecialProfileViewHolder(
    view: View
) : BaseViewHolder<PersonInfo>(view) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val person = item.person
        val personProfile = person.extraInfo?.profile ?: return

        personProfile.forEach {
            val view =
                LayoutInflater.from(context).inflate(R.layout.view_person_profile_item, null, false)
                    .apply {
                        layoutParams = Constraints.LayoutParams(
                            Constraints.LayoutParams.MATCH_PARENT,
                            Constraints.LayoutParams.WRAP_CONTENT
                        )
                    }

            view.profileContent.setupLinkableTextView(context)
            view.profileItemTitle.setResourceText(it.years)
            view.profileContent.setResourceStyledText(it.content)

            itemView.personProfileContainer.addView(view)
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_special_profile
    }
}