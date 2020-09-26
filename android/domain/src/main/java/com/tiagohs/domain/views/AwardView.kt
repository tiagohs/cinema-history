package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentNominee
import com.tiagohs.entities.main_topics.AwardMainTopic

interface AwardView : IView {

    fun setupArguments()
    fun bindAwardsNomineesContent(awardMainTopic: AwardMainTopic)
    fun startLoading()
    fun hideLoading()
}