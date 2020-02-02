package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.TimelineView

interface TimelinePresenter: IPresenter<TimelineView> {

    fun fetchTimeline(id: Int)
}