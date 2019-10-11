package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.MainTopicsView

interface MainTopicsPresenter: IPresenter<MainTopicsView> {

    fun fetchMainTopics()
}