package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.ui.configs.IView

interface TimelineView: IView {
    fun bindTimeline(timelines: List<Timeline>)

    fun startLoading()
    fun hideLoading()
}