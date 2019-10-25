package com.tiagohs.cinema_history.ui.adapters.timeline

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.TimelineType
import com.tiagohs.cinema_history.helpers.extensions.*
import com.tiagohs.cinema_history.models.contents.ContentInformation
import com.tiagohs.cinema_history.models.timeline.TimelineItem

class TimelineItemViewHolder(val context: Context?, view: View): RecyclerView.ViewHolder(view) {

    private val yearView: TextView = itemView.findViewById(R.id.year)
    private val descriptionView: TextView = itemView.findViewById(R.id.itemDescription)
    private val titleView: TextView = itemView.findViewById(R.id.itemTitle)

    private val imageView: ImageView = itemView.findViewById(R.id.image)
    private val imageContainer: ConstraintLayout = itemView.findViewById(R.id.imageContainer)
    private val imageShadow = itemView.findViewById<View>(R.id.imageShadow)
    private val imageCard = itemView.findViewById<CardView>(R.id.imageCard)
    private val textLine = itemView.findViewById<CardView>(R.id.textLine)

    private val centerLine: ConstraintLayout = itemView.findViewById(R.id.centerLine)
    private val centerBackgroundLine: ConstraintLayout = itemView.findViewById(R.id.centerBackgroundLine)

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

        bindTemplateColor(timelineItem)
    }

    private fun bindImage(timelineItem: TimelineItem) {
        val image = timelineItem.image ?: return
        val color = timelineItem.backgroundColor ?: return
        val transparentColor = timelineItem.backgroundTransparentColor ?: return
        val type = timelineItem.type
        val imageTransparent = timelineItem.imageTransparent

        image.imageStyle?.height?.let { changeImageDimens(it, type, imageTransparent) }
        imageView.loadImage(image)

        if (imageTransparent) {
            bindTransparentImage(color)
        } else {
            bindDefaultImage(color, transparentColor)
        }

        imageContainer.visibility = View.VISIBLE
    }

    private fun changeImageDimens(height: Int, type: TimelineType, imageTransparent: Boolean) {
        val containerLayoutParams = ConstraintLayout.LayoutParams(0.convertIntToDp(context), height.convertIntToDp(context))
        val marginSides = if (imageTransparent) 0 else 10.convertIntToDp(context)

        when (type) {
            TimelineType.TITLE -> {}
            TimelineType.RIGHT -> {
                containerLayoutParams.setMargins(marginSides, 50.convertIntToDp(context), marginSides, 0)

                containerLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                containerLayoutParams.endToStart = if (imageTransparent) guidelineRootDivisorLeft.id else guidelineRootDivisorCenter.id
                containerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                containerLayoutParams.bottomToTop = imageLegendContainer.id
            }
            TimelineType.LEFT -> {
                containerLayoutParams.setMargins(marginSides, 50.convertIntToDp(context), marginSides, 0)

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

        centerLine.setBackgroundColor(context.getResourceColor(color))
    }

    private fun bindDefaultImage(color: String, transparentColor: String) {
        val context = context ?: return

        imageShadow.visibility = View.VISIBLE
        imageCard.elevation = 5f
        imageCard.cardElevation = 5f
        imageCard.radius = 15f

        centerBackgroundLine.setBackgroundColor(context.getResourceColor(color))
        centerLine.setBackgroundColor(context.getResourceColor(transparentColor))
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

    private fun bindTemplateColor(timelineItem: TimelineItem) {
        val context = context ?: return
        val colorIdentifier = timelineItem.backgroundColor ?: return
        val colorTransparentIdentifier = timelineItem.backgroundTransparentColor ?: return
        val color = context.getResourceColor(colorIdentifier)
        val colorTransparent = context.getResourceColor(colorTransparentIdentifier)

        titleView.setLinkTextColor(color)
        descriptionView.setLinkTextColor(color)
        imageShadow.setBackgroundColor(colorTransparent)
        textLine.setCardBackgroundColor(color)

        centerBackgroundLine.setBackgroundColor(color)
        centerLine.setBackgroundColor(colorTransparent)
    }
}