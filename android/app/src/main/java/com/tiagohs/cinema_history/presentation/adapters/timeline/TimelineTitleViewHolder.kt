package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.view.View
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineTitle
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.adapter_timeline_title.*
import kotlinx.android.synthetic.main.adapter_timeline_title.view.*

class TimelineTitleViewHolder(
    val color: String,
    private val onNextClicked: (() -> Unit)?,
    private val onPreviousClicked: (() -> Unit)?,
    view: View
) : BaseViewHolder<Timeline>(view) {

    init {
        bindColors()
    }

    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timeline = item as? TimelineTitle ?: return

        title1.setResourceStyledText(timeline.title)

        bindDirectionButtons(timeline)
    }

    private fun bindDirectionButtons(timeline: TimelineTitle) {
        timeline.next?.let {
            nextContainer.show()
            nextContainer?.setOnClickListener { onNextClicked?.invoke() }
        }
        timeline.previous?.let {
            previousContainer.show()
            previousContainer?.setOnClickListener { onPreviousClicked?.invoke() }
        }
    }

    private fun bindColors() {
        val context = containerView.context ?: return
        val colorRes = context.getResourceColor(color)

        textLine.setCardBackgroundColor(colorRes)
        title2.setTextColor(colorRes)
    }
}