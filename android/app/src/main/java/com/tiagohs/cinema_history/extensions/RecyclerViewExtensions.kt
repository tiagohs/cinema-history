package com.tiagohs.cinema_history.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.presentation.adapters.MovieItemSpecialAdapter


fun RecyclerView.setupParallaxScrollListener() {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = layoutManager as? LinearLayoutManager ?: return

            val scrollOffset = recyclerView.computeHorizontalScrollOffset()
            val offsetFactor = (scrollOffset % measuredWidth) / measuredWidth.toFloat()

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            findViewHolderForAdapterPosition(firstVisibleItemPosition)?.let {
                (it as? MovieItemSpecialAdapter.MovieItemViewHolder)?.offset = -offsetFactor
            }

            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            if (firstVisibleItemPosition != lastVisibleItemPosition) {
                findViewHolderForAdapterPosition(lastVisibleItemPosition)?.let {
                    (it as? MovieItemSpecialAdapter.MovieItemViewHolder)?.offset = 1 - offsetFactor
                }
            }
        }
    })
}