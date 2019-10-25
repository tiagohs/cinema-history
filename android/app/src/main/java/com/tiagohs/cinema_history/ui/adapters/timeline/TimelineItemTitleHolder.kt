package com.tiagohs.cinema_history.ui.adapters.timeline

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.models.timeline.TimelineTitle
import kotlinx.android.synthetic.main.adapter_timeline_item_title.view.*

class TimelineItemTitleHolder(val context: Context?, view: View): RecyclerView.ViewHolder(view) {

    fun bind(timeline: TimelineTitle) {
        val context = context ?: return
        val colorIdentifier = timeline.backgroundColor ?: return
        val color = context.getResourceColor(colorIdentifier)

        itemView.centerLine.setBackgroundColor(color)
        itemView.textLine.setCardBackgroundColor(color)
        itemView.title2.setTextColor(color)
    }
}