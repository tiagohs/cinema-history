package com.tiagohs.cinema_history.presentation.adapters.timeline

import com.tiagohs.cinema_history.databinding.AdapterTimelineItemBinding
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineItem
import com.tiagohs.helpers.extensions.*

class TimelineItemViewHolder(
    val color: String,
    val textColor: String,
    private val binding: AdapterTimelineItemBinding
) : BaseViewHolder<Timeline>(binding) {

    init {
        bindColors()
    }

    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timelineItem = item as? TimelineItem ?: return
        val context = itemView.context ?: return

        binding.itemDescription.setupLinkableTextView(context)

        binding.year.setResourceText(timelineItem.year)
        binding.itemDescription.setResourceStyledText(timelineItem.description)

        timelineItem.title?.let {
            binding.titleContainer.show()

            binding.title.setupLinkableTextView(context)
            binding.title.setResourceStyledText(it)
        }

        val marginTop =
            timelineItem.marginTop?.convertIntToDp(context) ?: 16.convertIntToDp(context)
        binding.year.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToStart = binding.guidelineVertical.id
            setMargins(0, marginTop, 0, 0)
        }

        timelineItem.image?.let { bindImage(timelineItem) }
    }

    private fun bindImage(timelineItem: TimelineItem) {
        val timelineImage = timelineItem.image ?: return
        val imageTransparent = timelineItem.imageTransparent

        if (imageTransparent) {
            binding.image.loadImage(timelineImage, null)
        } else {
            binding.image.loadImageBlackAndWhite(timelineImage, null)
        }
    }


    private fun bindColors() {
        val context = itemView.context ?: return
        val textColorRes = context.getResourceColor(textColor)

        binding.textLine.setCardBackgroundColor(textColorRes)
        binding.titleContainer.setResourceBackgroundColor(color)
        binding.itemDescription.setLinkTextColor(context.getResourceColor(color))
        binding.title.setTextColor(textColorRes)
    }
}