package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.entities.main_topics.MainTopicItem

interface UniversalLinkView : IView {

    fun startHomeActivity()
    fun setupContentFromUniversalLink()
    fun startTimelineScreen(timelinePosition: String)
    fun startHistoryPage(mainTopic: MainTopicItem, itemSelectedPosition: Int)
    fun startMovieDetails(movieId: String)
    fun startPersonDetails(personId: String)
    fun startAwardDetails(awardMainTopic: AwardMainTopic)
}