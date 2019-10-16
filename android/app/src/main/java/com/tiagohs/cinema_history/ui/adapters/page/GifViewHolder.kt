package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadGif
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.contents.ContentGif
import kotlinx.android.synthetic.main.adapter_page_gif.view.*

class GifViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    var loaded = false

    fun bind(gifContent: ContentGif) {

        if (!loaded) {
            itemView.gifImage.loadGif(gifContent.gifImage)

            loaded = true
        }

        setupContentFooterInformation(gifContent.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_gif
    }
}