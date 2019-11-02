package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.models.timeline.TimelineResult
import com.tiagohs.cinema_history.ui.configs.IView

interface TimelineView: IView {

    fun setupArguments()

    fun bindTimeline(timelines: TimelineResult)

    fun startLoading()
    fun hideLoading()
}