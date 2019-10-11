package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.MainTopicItem
import com.tiagohs.cinema_history.ui.configs.IView

interface MainTopicsView: IView {

    fun bindMainTopics(mainTopics: List<MainTopic>)
}