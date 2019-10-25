package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageScaleType
import com.tiagohs.cinema_history.enums.TimelineType
import com.tiagohs.cinema_history.helpers.extensions.*
import com.tiagohs.cinema_history.models.contents.ContentInformation
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.models.timeline.TimelineHeader
import com.tiagohs.cinema_history.models.timeline.TimelineItem
import com.tiagohs.cinema_history.models.timeline.TimelineTitle
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineItemHeaderViewHolder
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineItemTitleHolder
import com.tiagohs.cinema_history.ui.adapters.timeline.TimelineItemViewHolder
import kotlin.math.abs

class TimelineAdapter(
    val context: Context?,
    val list: List<Timeline>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            TimelineType.TITLE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_title, parent, false)

                return TimelineItemTitleHolder(context, view)
            }
            TimelineType.HEADER.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_header, parent, false)

                return TimelineItemHeaderViewHolder(context, view)
            }
            TimelineType.RIGHT.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_right, parent, false)

                return TimelineItemViewHolder(context, view)
            }
            TimelineType.LEFT.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_left, parent, false)

                return TimelineItemViewHolder(context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_title, parent, false)

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
            TimelineType.HEADER.ordinal -> {
                val timelineHeader = list[position] as? TimelineHeader ?: return
                val timelineItemHeaderHolder = holder as? TimelineItemHeaderViewHolder ?: return

                timelineItemHeaderHolder.bind(timelineHeader)
            }
            TimelineType.RIGHT.ordinal, TimelineType.LEFT.ordinal -> {
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
