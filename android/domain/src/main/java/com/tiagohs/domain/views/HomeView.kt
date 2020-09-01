package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.HomeContentItem

interface HomeView: IView {
    fun setupContentView()
    fun bindHomeContent(homeContentList: List<HomeContentItem>)

    fun startLoading()
    fun hideLoading()
}