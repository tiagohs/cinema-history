package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.TimelineType
import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.models.timeline.TimelineItem
import com.tiagohs.cinema_history.models.timeline.TimelineTitle
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineItemTitleHolder
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineItemViewHolder

class TimelineAdapter(
    val context: Context?,
    val list: List<Timeline>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onNextClicked: (() -> Unit)? = null
    var onPreviousClicked: (() -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            TimelineType.TITLE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_title, parent, false)

                return TimelineItemTitleHolder(context, onNextClicked, onPreviousClicked, view)
            }
            TimelineType.ITEM.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item, parent, false)

                return TimelineItemViewHolder(context, view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item, parent, false)

                return TimelineItemViewHolder(context, view)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        when (viewType) {
            TimelineType.TITLE.ordinal -> {
                val timelineTitle = list[position] as? TimelineTitle ?: return
                val timelineItemTitleHolder = holder as? TimelineItemTitleHolder ?: return

                timelineItemTitleHolder.bind(timelineTitle)
            }
            TimelineType.ITEM.ordinal -> {
                val timelineItem = list[position] as? TimelineItem ?: return
                val timelineViewHolder = holder as? TimelineItemViewHolder ?: return

                timelineViewHolder.bind(timelineItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type.ordinal
    }

}
