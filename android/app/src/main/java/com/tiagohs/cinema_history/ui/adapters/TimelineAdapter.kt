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
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentInformation
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.models.timeline.TimelineItem
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

                return TimelineItemTitleHolder(view)
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

    inner class TimelineItemTitleHolder(view: View): RecyclerView.ViewHolder(view) {}

    inner class TimelineItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val yearView: TextView = itemView.findViewById(R.id.year)
        private val descriptionView: TextView = itemView.findViewById(R.id.itemDescription)
        private val titleView: TextView = itemView.findViewById(R.id.itemTitle)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val imageContainer: ConstraintLayout = itemView.findViewById(R.id.imageContainer)
        private val imageShadow = itemView.findViewById<View>(R.id.imageShadow)
        private val imageCard = itemView.findViewById<CardView>(R.id.imageCard)
        private val centerLine: ConstraintLayout = itemView.findViewById(R.id.centerLine)
        private val guidelineRootDivisorCenter: View = itemView.findViewById(R.id.guidelineRootDivisorCenter)
        private val guidelineRootDivisorRight: View = itemView.findViewById(R.id.guidelineRootDivisorRight)
        private val guidelineRootDivisorLeft: View = itemView.findViewById(R.id.guidelineRootDivisorLeft)

        private val imageLegendContainer: View = itemView.findViewById(R.id.imageLegendContainer)
        private val imageInfoContainer: ConstraintLayout = itemView.findViewById(R.id.imageLegendContainer)
        private val imageInfoTitle: TextView = itemView.findViewById(R.id.footerTitle)
        private val imageInfoDescription: TextView = itemView.findViewById(R.id.footerText)
        private val imageInfoSource: TextView = itemView.findViewById(R.id.footerReference)

        fun bind(timelineItem: TimelineItem) {
            descriptionView.setupLinkableTextView()

            yearView.text = timelineItem.year
            descriptionView.text = timelineItem.description.styledString()

            timelineItem.title?.let {

                titleView.setupLinkableTextView()

                titleView.visibility = View.VISIBLE
                titleView.text = it.styledString()
            }

            timelineItem.image?.let { bindImage(timelineItem) }
            timelineItem.imageInfo?.let { bindContentInfo(it) }
        }

        private fun bindImage(timelineItem: TimelineItem) {
            val image = timelineItem.image ?: return
            val type = timelineItem.type
            val imageTransparent = timelineItem.imageTransparent
            val color = timelineItem.backgroundColor

            image.imageStyle?.height?.let { changeImageDimens(it, type, imageTransparent) }
            imageView.loadImage(image)

            if (imageTransparent) {
                bindTransparentImage(color)
            }

            imageContainer.visibility = View.VISIBLE
        }

        private fun changeImageDimens(height: Int, type: TimelineType, imageTransparent: Boolean) {
            val containerLayoutParams = ConstraintLayout.LayoutParams(0.convertIntToDp(context), height.convertIntToDp(context))

            when (type) {
                TimelineType.TITLE -> {}
                TimelineType.RIGHT -> {
                    val marginRight = if (imageTransparent) 0 else 10.convertIntToDp(context)
                    containerLayoutParams.setMargins(10.convertIntToDp(context), 50.convertIntToDp(context), marginRight, 0)

                    containerLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    containerLayoutParams.endToStart = if (imageTransparent) guidelineRootDivisorLeft.id else guidelineRootDivisorCenter.id
                    containerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    containerLayoutParams.bottomToTop = imageLegendContainer.id
                }
                TimelineType.LEFT -> {
                    val marginLeft = if (imageTransparent) 0 else 10.convertIntToDp(context)
                    containerLayoutParams.setMargins(marginLeft, 50.convertIntToDp(context), 10.convertIntToDp(context), 0)

                    containerLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    containerLayoutParams.startToEnd = if (imageTransparent) guidelineRootDivisorRight.id else guidelineRootDivisorCenter.id
                    containerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    containerLayoutParams.bottomToTop = imageLegendContainer.id
                }
            }

            imageContainer.layoutParams = containerLayoutParams
        }

        private fun bindTransparentImage(color: String) {
            val context = context ?: return

            imageShadow.visibility = View.GONE
            imageCard.elevation = 0f
            imageCard.cardElevation = 0f
            imageCard.radius = 0f

            val backgroundColor = context.resources.getIdentifier(color, "color", context.packageName)
            centerLine.setBackgroundColor(context.resources.getColor(backgroundColor))
        }

        private fun bindContentInfo(contentInformation: ContentInformation) {
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
