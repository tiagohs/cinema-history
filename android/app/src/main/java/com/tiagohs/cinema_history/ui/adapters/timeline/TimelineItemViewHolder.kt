package com.tiagohs.cinema_history.ui.adapters.timeline

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.helpers.extensions.*
import com.tiagohs.cinema_history.models.timeline.TimelineItem
import kotlinx.android.synthetic.main.adapter_timeline_item.view.*

class TimelineItemViewHolder(
    val context: Context?,
    val color: String,
    val textColor: String,
    view: View): RecyclerView.ViewHolder(view) {

    init {
        bindColors()
    }

    fun bind(timelineItem: TimelineItem) {
        itemView.itemDescription.setupLinkableTextView(context)

        itemView.year.text = timelineItem.year
        itemView.itemDescription.text = timelineItem.description.styledString()

        timelineItem.title?.let {
            itemView.titleContainer.visibility = View.VISIBLE

            itemView.title.setupLinkableTextView(context)
            itemView.title.text = it.styledString()
        }

        timelineItem.image?.let { bindImage(timelineItem) }
    }

    private fun bindImage(timelineItem: TimelineItem) {
        val image = timelineItem.image ?: return
        val imageTransparent = timelineItem.imageTransparent

        if (imageTransparent) {
            itemView.image.loadImage(image, null)
        } else {
            itemView.image.loadImageBlackAndWhite(image, null)
        }
    }


    private fun bindColors() {
        val context = context ?: return
        val colorRes = context.getResourceColor(color)
        val textColorRes = context.getResourceColor(textColor)

        itemView.textLine.setCardBackgroundColor(textColorRes)
        itemView.titleContainer.setBackgroundColor(colorRes)
        itemView.title.setTextColor(textColorRes)
    }
}