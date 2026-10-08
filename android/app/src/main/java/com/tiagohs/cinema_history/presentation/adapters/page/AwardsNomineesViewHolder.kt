package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageAwardNomineesBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.presentation.adapters.NomineeAdapter
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentNominee
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration

class AwardsNomineesViewHolder(
    private val binding: AdapterPageAwardNomineesBinding,
    private val onNomineeClicked: ((nominee: Nominee) -> Unit)?
) : BasePageViewHolder(binding) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)

        val contentNominee = item as? ContentNominee ?: return

        binding.awardTitle.setResourceText(item.name)
        binding.awardList.apply {
            adapter =
                NomineeAdapter(contentNominee.nomineeList ?: emptyList(), onNomineeClicked)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        if (!isSetup) {
            binding.awardList.addItemDecoration(
                SpaceOffsetDecoration(
                    13.convertIntToDp(itemView.context),
                    SpaceOffsetDecoration.LEFT
                )
            )

            isSetup = true
        }

    }
}