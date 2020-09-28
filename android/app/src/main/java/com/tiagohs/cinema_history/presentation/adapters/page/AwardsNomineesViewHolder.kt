package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.NomineeAdapter
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentNominee
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.adapter_page_award_nominees.*

class AwardsNomineesViewHolder(
    val view: View,
    private val onNomineeClicked: ((nominee: Nominee) -> Unit)?
) : BasePageViewHolder(view) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)

        val contentNominee = item as? ContentNominee ?: return

        awardTitle.setResourceText(item.name)
        awardList.apply {
            adapter =
                NomineeAdapter(contentNominee.nomineeList ?: emptyList(), onNomineeClicked)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        if (!isSetup) {
            awardList.addItemDecoration(
                SpaceOffsetDecoration(
                    13.convertIntToDp(containerView.context),
                    SpaceOffsetDecoration.LEFT
                )
            )

            isSetup = true
        }

    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_award_nominees
    }
}