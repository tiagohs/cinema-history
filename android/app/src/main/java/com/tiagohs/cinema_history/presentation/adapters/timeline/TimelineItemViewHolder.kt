package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineItem
import com.tiagohs.helpers.extensions.*
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

        itemView.itemDescription.setupLinkableTextView(context)

        itemView.year.setResourceText(timelineItem.year)
        itemView.itemDescription.setResourceStyledText(timelineItem.description)

        timelineItem.title?.let {
            itemView.titleContainer.show()

            itemView.title.setupLinkableTextView(context)
            itemView.title.setResourceStyledText(it)
        }

        val marginTop =
            timelineItem.marginTop?.convertIntToDp(context) ?: 16.convertIntToDp(context)
        itemView.year.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToStart = itemView.guidelineVertical.id
            setMargins(0, marginTop, 0, 0)
        }

        timelineItem.image?.let { bindImage(timelineItem) }
    }

    private fun bindImage(timelineItem: TimelineItem) {
        val image = timelineItem.image ?: return
        val imageTransparent = timelineItem.imageTransparent

        if (imageTransparent) {
            itemView.image.loadImage(image, null)
        } else {
            itemView.image.loadImageBlackAndWhite(image, null)
        }
    }


    private fun bindColors() {
        val context = containerView.context ?: return
        val textColorRes = context.getResourceColor(textColor)

        itemView.textLine.setCardBackgroundColor(textColorRes)
        itemView.titleContainer.setResourceBackgroundColor(color)
        itemView.title.setTextColor(textColorRes)
    }
}