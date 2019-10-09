package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.contents.ContentGif
import kotlinx.android.synthetic.main.adapter_page_gif.view.*

class GifViewHolder(
    val context: Context?,
    val view: View
): RecyclerView.ViewHolder(view) {

    fun bind(content: ContentGif) {
        val context = this.context ?: return
        val gifUrl = content.path

        Glide.with(context)
            .asGif()
            .load(gifUrl)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(itemView.gifImage)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_gif
    }
}