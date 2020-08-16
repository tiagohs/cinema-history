package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentGif
import kotlinx.android.synthetic.main.adapter_page_gif.view.*


class GifViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val gifContent = item as? ContentGif ?: return
        val imageThumbnail = gifContent.gifImage.thumbnail

        itemView.gifViewer.setupGif(gifContent.gifImage, imageThumbnail)
        setupContentFooterInformation(gifContent.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_gif
    }
}