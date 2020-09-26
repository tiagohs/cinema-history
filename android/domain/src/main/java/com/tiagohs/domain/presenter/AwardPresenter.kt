package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.AwardView
import com.tiagohs.entities.main_topics.AwardMainTopic

interface AwardPresenter : IPresenter<AwardView> {
    fun fetchAwardsNominees(awardMainTopic: AwardMainTopic?)
}