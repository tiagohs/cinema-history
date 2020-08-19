package com.tiagohs.cinema_history.presentation.adapters.timeline

import android.view.View
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineFooter
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.adapter_timeline_footer.*
import kotlinx.android.synthetic.main.adapter_timeline_footer.view.*

class TimelineItemFooterHolder(
    private val onNextClicked: (() -> Unit)?,
    private val onPreviousClicked: (() -> Unit)?,
    view: View
) : BaseViewHolder<Timeline>(view) {

    override fun bind(item: Timeline, position: Int) {
        super.bind(item, position)
        val timeline = item as? TimelineFooter ?: return

        bindDirectionButtons(timeline)
    }

    private fun bindDirectionButtons(timeline: TimelineFooter) {
        timeline.next?.let {
            nextContainer.show()
            nextContainer?.setOnClickListener { onNextClicked?.invoke() }
        }
        timeline.previous?.let {
            previousContainer.show()
            previousContainer?.setOnClickListener { onPreviousClicked?.invoke() }
        }
    }

}