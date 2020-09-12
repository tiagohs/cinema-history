package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.ImageAdapter
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentSlide
import com.tiagohs.helpers.extensions.convertIntToDp
import cz.intik.overflowindicator.SimpleSnapHelper
import kotlinx.android.synthetic.main.adapter_page_slide.*


class SlideViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val contentSlide = item as? ContentSlide ?: return

        contentSlide.height?.let {
            imageList.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                it.convertIntToDp(context)
            )
        }

        imageList.apply {
            adapter = ImageAdapter(contentSlide.images)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            setupParallaxScrollListener()
        }

        imageList.onFlingListener = null
        imageListIndicator.attachToRecyclerView(imageList)
        SimpleSnapHelper(imageListIndicator).attachToRecyclerView(imageList)

        setupContentFooterInformation(contentSlide.information)
    }

    override fun onDestroy() {
        super.onDestroy()
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