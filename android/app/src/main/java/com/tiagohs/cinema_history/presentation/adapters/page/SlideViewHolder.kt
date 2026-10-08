package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPageSlideBinding
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.presentation.adapters.ImageAdapter
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentSlide
import com.tiagohs.helpers.extensions.convertIntToDp
import cz.intik.overflowindicator.SimpleSnapHelper


class SlideViewHolder(
    private val binding: AdapterPageSlideBinding
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val contentSlide = item as? ContentSlide ?: return

        contentSlide.height?.let {
            binding.imageList.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                it.convertIntToDp(context)
            )
        }

        binding.imageList.apply {
            adapter = ImageAdapter(contentSlide.images)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            setupParallaxScrollListener()
        }

        binding.imageList.onFlingListener = null
        binding.imageListIndicator.attachToRecyclerView(binding.imageList)
        SimpleSnapHelper(binding.imageListIndicator).attachToRecyclerView(binding.imageList)

        setupContentFooterInformation(binding.footerContainer, contentSlide.information)
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
}