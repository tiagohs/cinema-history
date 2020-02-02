package com.tiagohs.cinema_history.presentation.adapters.page

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.ContentSlide
import com.tiagohs.cinema_history.presentation.adapters.ImageAdapter
import com.tiagohs.helpers.extensions.convertIntToDp
import kotlinx.android.synthetic.main.adapter_page_slide.view.*
import cz.intik.overflowindicator.SimpleSnapHelper


class SlideViewHolder(
    val context: Context?,
    val view: View
): BasePageViewHolder(view) {

    fun bind(contentSlide: ContentSlide) {
        val context = context ?: return

        contentSlide.height?.let {
            val imageListLayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, it.convertIntToDp(context))

            itemView.imageList.layoutParams = imageListLayoutParams
        }

        itemView.imageList.apply {
            adapter = ImageAdapter(context, contentSlide.images)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            itemView.imageListIndicator.attachToRecyclerView(this)
            SimpleSnapHelper(itemView.imageListIndicator).attachToRecyclerView(this)

            setupParallaxScrollListener()
        }

    }

    private fun RecyclerView.setupParallaxScrollListener() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                findViewHolderForAdapterPosition(firstVisibleItemPosition)?.let {
                    it.itemView.translationX = (-it.itemView.left / 2).toFloat()
                }
            }
        })
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_slide
    }
}