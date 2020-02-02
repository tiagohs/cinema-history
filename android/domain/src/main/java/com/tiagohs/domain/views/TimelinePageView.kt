package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView

interface TimelinePageView: IView {

    fun bindTimelineIDs(list: List<Int>)
}