package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentImage
import com.tiagohs.entities.image.Image
import com.tiagohs.helpers.extensions.loadImage
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.*

class ImageViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentImage = item as? ContentImage ?: return

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