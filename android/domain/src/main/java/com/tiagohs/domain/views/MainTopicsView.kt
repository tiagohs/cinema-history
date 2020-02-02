package com.tiagohs.domain.views

import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.domain.views.configs.IView

interface MainTopicsView: IView {

    fun setupArguments()
    fun setupScreenTitle()
    fun setupScreenLayout()

    fun bindMainTopics(mainTopics: List<MainTopic>)

    fun startLoading()
    fun hideLoading()
}