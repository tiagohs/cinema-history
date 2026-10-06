package com.tiagohs.cinema_history.presentation.adapters.person_details

import com.tiagohs.cinema_history.databinding.AdapterPersonInfoBiographyBinding
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.helpers.extensions.setResourceText

class PersonInfoBiographyViewHolder(
    private val binding: AdapterPersonInfoBiographyBinding
) : BaseViewHolder<PersonInfo>(binding) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)

        binding.personBiography.setResourceText(item.person.biography)
    }
}