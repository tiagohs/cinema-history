package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.awards.NomineeResult
import com.tiagohs.entities.main_topics.AwardMainTopic

interface AwardView : IView {

    fun setupArguments()
    fun bindAwardsNomineesContent(awardMainTopic: AwardMainTopic)
    fun bindAwardYear(year: String, result: NomineeResult)
    fun onAwardYearError(year: String, error: Throwable)
    fun startLoading()
    fun hideLoading()
}
