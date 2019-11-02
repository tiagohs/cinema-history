package com.tiagohs.cinema_history.ui.adapters.timeline

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.timeline.TimelineHeader
import kotlinx.android.synthetic.main.adapter_timeline_item_header.view.*

class TimelineItemHeaderViewHolder(val context: Context?, view: View): RecyclerView.ViewHolder(view) {

    fun bind(timelineHeader: TimelineHeader) {
        val context = context ?: return

        itemView.headerTitle.text = timelineHeader.title
        itemView.headerSubtitle.text = timelineHeader.subtitle

        itemView.headerTitle.setTextColor(context.getResourceColor(timelineHeader.titleColor))
        itemView.headerSubtitle.setTextColor(context.getResourceColor(timelineHeader.subtitleColor))

        itemView.image.loadImage(timelineHeader.image)
    }
}
