package com.tiagohs.domain.presenter

import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.PresentationView

interface PresentationPresenter: IPresenter<PresentationView> {
    fun fetchMoviesByListId(mainTopic: MainTopicItem?)
}