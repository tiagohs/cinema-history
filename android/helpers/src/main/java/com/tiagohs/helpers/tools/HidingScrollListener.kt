package com.tiagohs.helpers.tools

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HidingScrollListener(
        private val callback: HidingScrollCallback,
        private val numberOfItens: Int
) : RecyclerView.OnScrollListener() {

    private val HIDE_THRESHOLD = 20
    private var scrolledDistance = 0
    private var controlsVisible = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            callback.onScrollUp()
            controlsVisible = false
            scrolledDistance = 0
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            callback.onScrollDown()
            controlsVisible = true
            scrolledDistance = 0
        }

        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy
        }

        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        if (layoutManager.findLastCompletelyVisibleItemPosition() == numberOfItens) {
            callback.onLastItemCompletelyVisible()
        }

    }

    interface HidingScrollCallback {
        fun onScrollUp()
        fun onScrollDown()
        fun onLastItemCompletelyVisible()
    }
}
