package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.domain.views.MainTopicsView

interface MainTopicsPresenter: IPresenter<MainTopicsView> {

    fun fetchMainTopics(mainTopicsType: MainTopicsType?)
}