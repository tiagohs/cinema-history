package com.tiagohs.domain.views

import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.domain.views.configs.IView

interface PresentationView: IView {

    fun setupArguments()
    fun bindSumarioHeader()
    fun bindMainTopicPresentation(mainTopic: MainTopicItem?)
}