package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.HomeView

interface HomePresenter : IPresenter<HomeView> {
    fun fetchHomeContent()
}