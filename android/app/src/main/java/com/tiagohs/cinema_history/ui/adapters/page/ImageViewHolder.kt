package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentImage
import com.tiagohs.cinema_history.models.contents.ContentText
import com.tiagohs.cinema_history.models.image.Image
import kotlinx.android.synthetic.main.adapter_main_topics_card.view.*
import kotlinx.android.synthetic.main.adapter_page_gif.view.*

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