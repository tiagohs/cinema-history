package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.TimelineType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentInformation
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.models.timeline.TimelineItem

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

                return TimelineItemViewHolder(view)
            }
            TimelineType.RIGHT.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_right, parent, false)

                return TimelineItemViewHolder(view)
            }
            TimelineType.LEFT.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_left, parent, false)

                return TimelineItemViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timeline_item_title, parent, false)

                return TimelineItemViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        when (viewType) {
//            TimelineType.TITLE.ordinal -> {
//
//            }
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

    inner class TimelineItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(timelineItem: TimelineItem) {
            val yearView = itemView.findViewById<TextView>(R.id.year)
            val descriptionView = itemView.findViewById<TextView>(R.id.itemDescription)

            descriptionView.setupLinkableTextView()

            yearView.text = timelineItem.year
            descriptionView.text = timelineItem.description.styledString()

            timelineItem.title?.let {
                val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
                titleView.setupLinkableTextView()

                titleView.visibility = View.VISIBLE
                titleView.text = it.styledString()
            }

            timelineItem.image?.let { bindImage(timelineItem.type, it) }
            timelineItem.imageInfo?.let { bindContentInfo(it) }
        }

        private fun bindImage(type: TimelineType, image: Image) {
            val imageView = itemView.findViewById<ImageView>(R.id.image)
            val imageContainer = itemView.findViewById<ConstraintLayout>(R.id.imageContainer)
            val guidelineRootDivisorCenter = itemView.findViewById<View>(R.id.guidelineRootDivisorCenter)
            val imageLegendContainer = itemView.findViewById<View>(R.id.imageLegendContainer)

            image.imageStyle?.height?.let {
                val containerLayoutParams = ConstraintLayout.LayoutParams(0.convertIntToDp(context), it.convertIntToDp(context))
                containerLayoutParams.setMargins(10.convertIntToDp(context), 50.convertIntToDp(context), 10.convertIntToDp(context), 0)

                when (type) {
                    TimelineType.TITLE -> {}
                    TimelineType.RIGHT -> {
                        containerLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        containerLayoutParams.endToStart = guidelineRootDivisorCenter.id
                        containerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        containerLayoutParams.bottomToTop = imageLegendContainer.id
                    }
                    TimelineType.LEFT -> {
                        containerLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        containerLayoutParams.startToEnd = guidelineRootDivisorCenter.id
                        containerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        containerLayoutParams.bottomToTop = imageLegendContainer.id
                    }
                }

                containerLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                containerLayoutParams.startToEnd = guidelineRootDivisorCenter.id
                containerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                containerLayoutParams.bottomToTop = imageLegendContainer.id

                imageContainer.layoutParams = containerLayoutParams
            }

            imageContainer.visibility = View.VISIBLE

            imageView.loadImage(image)
        }

        private fun bindContentInfo(contentInformation: ContentInformation) {
            val imageInfoContainer = itemView.findViewById<ConstraintLayout>(R.id.imageLegendContainer)
            val imageInfoTitle = itemView.findViewById<TextView>(R.id.footerTitle)
            val imageInfoDescription = itemView.findViewById<TextView>(R.id.footerText)
            val imageInfoSource = itemView.findViewById<TextView>(R.id.footerReference)

            imageInfoContainer.visibility = View.VISIBLE

            imageInfoDescription.setupLinkableTextView()
            imageInfoTitle.setupLinkableTextView()
            imageInfoSource.setupLinkableTextView()

            imageInfoTitle.text = contentInformation.contentTitle?.styledString()
            imageInfoDescription.text = contentInformation.contentText?.styledString()
            imageInfoSource.text = contentInformation.source?.styledString()
        }
    }
}
