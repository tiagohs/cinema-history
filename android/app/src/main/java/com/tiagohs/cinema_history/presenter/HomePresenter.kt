package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.HomeView

interface HomePresenter: IPresenter<HomeView> {

    fun fetchMainTopics()
}