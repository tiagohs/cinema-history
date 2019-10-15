package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.ui.configs.IView

interface PresentationView: IView {

    fun setupArguments()
    fun bindSumarioHeader()
    fun bindMainTopicPresentation(mainTopic: MainTopicItem?)
}