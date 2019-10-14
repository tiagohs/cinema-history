package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.graphics.Typeface
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.contents.ContentSlide
import com.tiagohs.cinema_history.models.contents.ContentText
import com.tiagohs.cinema_history.ui.adapters.ImageAdapter
import kotlinx.android.synthetic.main.adapter_page_slide.view.*
import kotlinx.android.synthetic.main.adapter_page_text.view.*

class SlideViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentSlide: ContentSlide) {
        val context = context ?: return

        itemView.imageList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemView.imageList.adapter = ImageAdapter(context, contentSlide.images)

        setupContentFooterInformation(contentSlide.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_slide
    }
}