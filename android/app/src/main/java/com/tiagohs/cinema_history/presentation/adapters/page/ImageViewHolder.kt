package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentImage
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setupPreview
import kotlinx.android.synthetic.main.adapter_image.view.*

class ImageViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentImage = item as? ContentImage ?: return

        itemView.image.loadImage(contentImage.image)
        itemView.image.setupPreview(contentImage.image)

        setupContentFooterInformation(contentImage.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_img
    }
}