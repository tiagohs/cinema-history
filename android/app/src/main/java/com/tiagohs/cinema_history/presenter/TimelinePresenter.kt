package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.TimelineView

interface TimelinePresenter: IPresenter<TimelineView> {
    fun fetchTimelineItems()
}