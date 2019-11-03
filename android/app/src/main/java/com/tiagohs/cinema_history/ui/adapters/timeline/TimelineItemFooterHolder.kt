package com.tiagohs.cinema_history.ui.adapters.timeline

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.models.timeline.TimelineFooter
import kotlinx.android.synthetic.main.adapter_timeline_footer.view.*

class TimelineItemFooterHolder(
    val context: Context?,
    private val onNextClicked: (() -> Unit)?,
    private val onPreviousClicked: (() -> Unit)?,
    view: View): RecyclerView.ViewHolder(view) {

    fun bind(timeline: TimelineFooter) {
        bindDirectionButtons(timeline)
    }

    private fun bindDirectionButtons(timeline: TimelineFooter) {
        timeline.next?.let {
            itemView.nextContainer.visibility = View.VISIBLE
            itemView.nextContainer?.setOnClickListener { onNextClicked?.invoke() }
        }
        timeline.previous?.let {
            itemView.previousContainer.visibility = View.VISIBLE
            itemView.previousContainer?.setOnClickListener { onPreviousClicked?.invoke() }
        }
    }

}