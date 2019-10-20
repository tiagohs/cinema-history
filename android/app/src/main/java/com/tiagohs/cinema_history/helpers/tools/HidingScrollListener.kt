package com.tiagohs.cinema_history.helpers.tools

import androidx.recyclerview.widget.RecyclerView


class HidingScrollListener(
        private val callback: HidingScrollCallback
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
    }

    interface HidingScrollCallback {
        fun onScrollUp()
        fun onScrollDown()
    }
}
