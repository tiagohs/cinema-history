package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.MainTopicsView
import com.tiagohs.cinema_history.ui.views.MilMoviesPresentationView

interface MainTopicsPresenter: IPresenter<MainTopicsView> {

    fun fetchMainTopics(mainTopicsType: MainTopicsType?)
}