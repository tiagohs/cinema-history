package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.HistoryPageView

interface HistoryPagePresenter: IPresenter<HistoryPageView> {

    fun fetchPageContent(mainTopicId: Int?, sumarioId: Int?)
}