package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.timeline.TimelineItem
import kotlinx.android.synthetic.main.adapter_timeline.view.*

class TimelineAdapter(
    val context: Context?,
    val list: List<TimelineItem>
): RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline, parent, false)

        return TimelineViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val timelineItem = list.get(position)

        holder.bind(timelineItem)
    }

    inner class TimelineViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(timelineItem: TimelineItem) {
            val context = context ?: return

            itemView.timelineHeader.background = getHeaderBackground(context)
        }

        private fun getHeaderBackground(context: Context): GradientDrawable {
            val background = GradientDrawable()

            background.setColor(context.resources.getColor(R.color.md_red_500))
            background.cornerRadius = 10f

            return background
        }
    }
}