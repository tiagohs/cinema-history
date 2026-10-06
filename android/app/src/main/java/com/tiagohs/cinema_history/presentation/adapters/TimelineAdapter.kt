package com.tiagohs.cinema_history.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.tiagohs.cinema_history.databinding.AdapterEmptyBinding
import com.tiagohs.cinema_history.databinding.AdapterTimelineFooterBinding
import com.tiagohs.cinema_history.databinding.AdapterTimelineItemBinding
import com.tiagohs.cinema_history.databinding.AdapterTimelineTitleBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.adapters.timeline.TimelineItemFooterHolder
import com.tiagohs.cinema_history.presentation.adapters.timeline.TimelineItemViewHolder
import com.tiagohs.cinema_history.presentation.adapters.timeline.TimelineTitleViewHolder
import com.tiagohs.cinema_history.presentation.fragments.TimelineCallbacks
import com.tiagohs.entities.enums.TimelineType
import com.tiagohs.entities.timeline.Timeline

class TimelineAdapter(
    list: List<Timeline>,
    val totalOfTimelines: Int,
    val color: String,
    val textColor: String,
    val callback: TimelineCallbacks
) : BaseAdapter<Timeline, BaseViewHolder<Timeline>>(list) {

    var onNextClicked: (() -> Unit)? = null
    var onPreviousClicked: (() -> Unit)? = null
    var onUpClicked: (() -> Unit)? = null
    var onDownClicked: (() -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<Timeline> =
        when (viewType) {
            TimelineType.TITLE.ordinal -> TimelineTitleViewHolder(
                color,
                onNextClicked,
                onPreviousClicked,
                onDownClicked,
                totalOfTimelines,
                callback,
                AdapterTimelineTitleBinding.inflate(inflater, parent, false)
            )
            TimelineType.ITEM.ordinal -> TimelineItemViewHolder(color, textColor, AdapterTimelineItemBinding.inflate(inflater, parent, false))
            TimelineType.FOOTER.ordinal -> TimelineItemFooterHolder(
                onNextClicked,
                onPreviousClicked,
                onUpClicked,
                totalOfTimelines,
                callback,
                AdapterTimelineFooterBinding.inflate(inflater, parent, false)
            )
            else -> object : BaseViewHolder<Timeline>(AdapterEmptyBinding.inflate(inflater, parent, false)) {}
        }

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}
