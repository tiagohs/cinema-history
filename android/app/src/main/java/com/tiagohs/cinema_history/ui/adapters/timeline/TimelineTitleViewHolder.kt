package com.tiagohs.cinema_history.ui.adapters.timeline

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.timeline.TimelineTitle
import kotlinx.android.synthetic.main.adapter_timeline_title.view.*

class TimelineTitleViewHolder(
    val context: Context?,
    val color: String,
    private val onNextClicked: (() -> Unit)?,
    private val onPreviousClicked: (() -> Unit)?,
    view: View): RecyclerView.ViewHolder(view) {

    init {
        bindColors()
    }

    fun bind(timeline: TimelineTitle) {
        itemView.title1.text = timeline.title.styledString()

        bindDirectionButtons(timeline)
    }

    private fun bindDirectionButtons(timeline: TimelineTitle) {
        timeline.next?.let {
            itemView.nextContainer.visibility = View.VISIBLE
            itemView.nextContainer?.setOnClickListener { onNextClicked?.invoke() }
        }
        timeline.previous?.let {
            itemView.previousContainer.visibility = View.VISIBLE
            itemView.previousContainer?.setOnClickListener { onPreviousClicked?.invoke() }
        }
    }

    private fun bindColors() {
        val context = context ?: return
        val colorRes = context.getResourceColor(color)

        itemView.textLine.setCardBackgroundColor(colorRes)
        itemView.title2.setTextColor(colorRes)
    }
}