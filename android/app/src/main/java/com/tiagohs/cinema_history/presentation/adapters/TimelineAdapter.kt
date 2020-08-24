package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.cinema_history.presentation.adapters.timeline.TimelineItemFooterHolder
import com.tiagohs.cinema_history.presentation.adapters.timeline.TimelineItemViewHolder
import com.tiagohs.cinema_history.presentation.adapters.timeline.TimelineTitleViewHolder
import com.tiagohs.entities.enums.TimelineType
import com.tiagohs.entities.timeline.Timeline

class TimelineAdapter(
    list: List<Timeline>,
    val totalOfTimelines: Int,
    val color: String,
    val textColor: String
) : BaseAdapter<Timeline, BaseViewHolder<Timeline>>(list) {

    var onNextClicked: (() -> Unit)? = null
    var onPreviousClicked: (() -> Unit)? = null
    var onUpClicked: (() -> Unit)? = null
    var onDownClicked: (() -> Unit)? = null

    init {
        setHasStableIds(true)
    }


    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        TimelineType.TITLE.ordinal -> R.layout.adapter_timeline_title
        TimelineType.ITEM.ordinal -> R.layout.adapter_timeline_item
        TimelineType.FOOTER.ordinal -> R.layout.adapter_timeline_footer
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): BaseViewHolder<Timeline> =
        when (viewType) {
            TimelineType.TITLE.ordinal -> TimelineTitleViewHolder(
                color,
                onNextClicked,
                onPreviousClicked,
                onDownClicked,
                totalOfTimelines,
                view
            )
            TimelineType.ITEM.ordinal -> TimelineItemViewHolder(color, textColor, view)
            TimelineType.FOOTER.ordinal -> TimelineItemFooterHolder(
                onNextClicked,
                onPreviousClicked,
                onUpClicked,
                totalOfTimelines,
                view
            )
            else -> object : BaseViewHolder<Timeline>(view) {}
        }

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}
