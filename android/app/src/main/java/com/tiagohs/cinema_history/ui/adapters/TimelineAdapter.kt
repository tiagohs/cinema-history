package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.timeline.Timeline
import com.tiagohs.entities.timeline.TimelineFooter
import com.tiagohs.entities.timeline.TimelineItem
import com.tiagohs.entities.timeline.TimelineTitle
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineItemFooterHolder
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineTitleViewHolder
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineItemViewHolder
import com.tiagohs.entities.enums.TimelineType

class TimelineAdapter(
    val context: Context?,
    val list: List<Timeline>,
    val color: String,
    val textColor: String
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

                return TimelineTitleViewHolder(context, color, onNextClicked, onPreviousClicked, view)
            }
            TimelineType.ITEM.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item, parent, false)

                return TimelineItemViewHolder(context, color, textColor, view)
            }
            TimelineType.FOOTER.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_footer, parent, false)

                return TimelineItemFooterHolder(context, onNextClicked, onPreviousClicked, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item, parent, false)

                return TimelineItemViewHolder(context,color, textColor, view)
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
                val timelineItemTitleHolder = holder as? TimelineTitleViewHolder ?: return

                timelineItemTitleHolder.bind(timelineTitle)
            }
            TimelineType.FOOTER.ordinal -> {
                val timelineFooter = list[position] as? TimelineFooter ?: return
                val timelineItemFooterHolder = holder as? TimelineItemFooterHolder ?: return

                timelineItemFooterHolder.bind(timelineFooter)
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
