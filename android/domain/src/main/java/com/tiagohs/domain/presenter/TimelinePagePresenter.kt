package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.TimelinePageView

interface TimelinePagePresenter: IPresenter<TimelinePageView> {
    fun fetchTimelineItems()
}