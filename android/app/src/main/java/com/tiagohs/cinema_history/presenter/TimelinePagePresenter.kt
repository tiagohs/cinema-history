package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.TimelinePageView

interface TimelinePagePresenter: IPresenter<TimelinePageView> {
    fun fetchTimelineItems()
}