package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.PresentationView

interface PresentationPresenter: IPresenter<PresentationView> {
    fun fetchMoviesByListId(mainTopic: MainTopicItem?)
}