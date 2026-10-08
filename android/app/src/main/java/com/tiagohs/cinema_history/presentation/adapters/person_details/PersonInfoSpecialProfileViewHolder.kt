package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewPersonProfileItemBinding
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialProfileBinding
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.setResourceText

class PersonInfoSpecialProfileViewHolder(
    private val binding: AdapterPersonInfoSpecialProfileBinding
) : BaseViewHolder<PersonInfo>(binding) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val person = item.person
        val personProfile = person.extraInfo?.profile ?: return

        personProfile.forEach {
            val itemBinding = ViewPersonProfileItemBinding.inflate(LayoutInflater.from(context), null, false)
            itemBinding.root.layoutParams = Constraints.LayoutParams(
                Constraints.LayoutParams.MATCH_PARENT,
                Constraints.LayoutParams.WRAP_CONTENT
            )

            itemBinding.profileContent.setupLinkableTextView(context)
            itemBinding.profileItemTitle.setResourceText(it.years)
            itemBinding.profileContent.setResourceStyledText(it.content)

            binding.personProfileContainer.addView(itemBinding.root)
        }
    }
}