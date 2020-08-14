package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.ReferenceView
import com.tiagohs.domain.views.TimelineView

interface ReferencePresenter: IPresenter<ReferenceView> {

    fun fetchReferences()
}