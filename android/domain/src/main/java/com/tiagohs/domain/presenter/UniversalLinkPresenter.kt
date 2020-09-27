package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.UniversalLinkView

interface UniversalLinkPresenter : IPresenter<UniversalLinkView> {
    fun fetchMainTopicById(mainTopicId: String?, itemSelectedPosition: String?)
    fun fetchAwardById(awardID: String)
}