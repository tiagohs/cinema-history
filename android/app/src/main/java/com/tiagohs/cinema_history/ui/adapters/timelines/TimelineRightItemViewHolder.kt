package com.tiagohs.cinema_history.ui.adapters.timelines

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.contents.ContentInformation
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.timeline.TimelineItem
import kotlinx.android.synthetic.main.adapter_timeline_item_right.view.*

class TimelineRightItemViewHolder(val context: Context?, view: View): RecyclerView.ViewHolder(view) {

    fun bind(timelineItem: TimelineItem) {
//        val yearView = itemView.findViewById<TextView>(R.id.year)
//        val descriptionView = itemView.findViewById<TextView>(R.id.itemDescription)

        itemView.year.text = timelineItem.year
        itemView.itemDescription.text = timelineItem.description

        timelineItem.title?.let {
            val titleView = itemView.findViewById<TextView>(R.id.itemTitle)

            itemView.itemTitle.visibility = View.VISIBLE
            itemView.itemTitle.text = it
        }

        timelineItem.image?.let { bindImage(it) }
        timelineItem.imageInfo?.let { bindContentInfo(it) }
    }

    private fun bindImage(image: Image) {
        val imageView = itemView.findViewById<ImageView>(R.id.image)
        val imageContainer = itemView.findViewById<ConstraintLayout>(R.id.imageContainer)

        image.imageStyle?.height?.let {
            val containerLayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, it.convertIntToDp(context))
            containerLayoutParams.setMargins(10.convertIntToDp(context), 50.convertIntToDp(context), 10.convertIntToDp(context), 0)

            itemView.imageContainer.layoutParams = containerLayoutParams
        }

        itemView.imageContainer.visibility = View.VISIBLE

        itemView.image.loadImage(image)
    }

    private fun bindContentInfo(contentInformation: ContentInformation) {
        val imageInfoContainer = itemView.findViewById<ConstraintLayout>(R.id.imageLegendContainer)
        val imageInfoTitle = itemView.findViewById<TextView>(R.id.footerTitle)
        val imageInfoDescription = itemView.findViewById<TextView>(R.id.footerText)
        val imageInfoSource = itemView.findViewById<TextView>(R.id.footerReference)

        itemView.imageLegendContainer.visibility = View.VISIBLE

        itemView.footerText.text = contentInformation.contentTitle
        itemView.footerText.text = contentInformation.contentText
        itemView.footerReference.text = contentInformation.source
    }
}