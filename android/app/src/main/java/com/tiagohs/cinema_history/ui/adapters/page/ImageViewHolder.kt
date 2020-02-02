package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.entities.contents.ContentImage
import com.tiagohs.entities.image.Image
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.*

class ImageViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentImage: ContentImage) {
        val context = context ?: return

        itemView.mainImage.loadImage(contentImage.image)

        itemView.mainImage.setOnClickListener {
            StfalconImageViewer.Builder<Image>(context, emptyList()) { view, image ->
                view.loadImage(image)
            }
                .allowZooming(true)
                .withTransitionFrom(itemView.mainImage)
                .show()
        }

        setupContentFooterInformation(contentImage.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_img
    }
}