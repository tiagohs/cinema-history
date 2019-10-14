package com.tiagohs.cinema_history.ui.adapters.page

import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.contents.ContentSlide
import com.tiagohs.cinema_history.ui.adapters.ImageAdapter
import com.tiagohs.cinema_history.ui.custom.ParallaxImageTransformer
import kotlinx.android.synthetic.main.adapter_page_slide.view.*

class SlideViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentSlide: ContentSlide) {
        val context = context ?: return

        itemView.imageList.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        itemView.imageList.adapter = ImageAdapter(context, contentSlide.images)
        itemView.imageList.setPageTransformer(ParallaxImageTransformer())

        setupContentFooterInformation(contentSlide.information)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_slide
    }
}