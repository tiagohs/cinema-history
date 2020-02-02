package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.ContentGif
import kotlinx.android.synthetic.main.adapter_page_gif.view.*



class GifViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(gifContent: ContentGif) {
        val imageThumbnail = gifContent.gifImage.thumbnail

        itemView.gifViewer.setupGif(gifContent.gifImage, imageThumbnail)

        setupContentFooterInformation(gifContent.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_gif
    }
}