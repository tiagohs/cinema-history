package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.ui.configs.IView

interface TimelinePageView: IView {

    fun bindTimelineIDs(list: List<Int>)
}