package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineItem
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_timeline_item.*
import kotlinx.android.synthetic.main.adapter_timeline_item.view.*

class TimelineItemViewHolder(
    val color: String,
    val textColor: String,
    view: View
) : BaseViewHolder<Timeline>(view) {

    init {
        bindColors()
    }

    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timelineItem = item as? TimelineItem ?: return
        val context = containerView.context ?: return

        itemDescription.setupLinkableTextView(context)

        year.setResourceText(timelineItem.year)
        itemDescription.setResourceStyledText(timelineItem.description)

        timelineItem.title?.let {
            titleContainer.show()

            title.setupLinkableTextView(context)
            title.setResourceStyledText(it)
        }

        val marginTop =
            timelineItem.marginTop?.convertIntToDp(context) ?: 16.convertIntToDp(context)
        year.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToStart = guidelineVertical.id
            setMargins(0, marginTop, 0, 0)
        }

        timelineItem.image?.let { bindImage(timelineItem) }
    }

    private fun bindImage(timelineItem: TimelineItem) {
        val timelineImage = timelineItem.image ?: return
        val imageTransparent = timelineItem.imageTransparent

        if (imageTransparent) {
            image.loadImage(timelineImage, null)
        } else {
            image.loadImageBlackAndWhite(timelineImage, null)
        }
    }


    private fun bindColors() {
        val context = containerView.context ?: return
        val textColorRes = context.getResourceColor(textColor)

        textLine.setCardBackgroundColor(textColorRes)
        titleContainer.setResourceBackgroundColor(color)
        title.setTextColor(textColorRes)
    }
}