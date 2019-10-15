package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.HistoryPageView

interface HistoryPagePresenter: IPresenter<HistoryPageView> {

    fun fetchPageContent(mainTopicId: Int?, sumarioId: Int?)
}