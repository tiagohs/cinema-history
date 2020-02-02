package com.tiagohs.domain.views

import com.tiagohs.entities.timeline.TimelineResult
import com.tiagohs.domain.views.configs.IView

interface TimelineView: IView {

    fun setupArguments()

    fun bindTimeline(timelines: TimelineResult)

    fun startLoading()
    fun hideLoading()
}