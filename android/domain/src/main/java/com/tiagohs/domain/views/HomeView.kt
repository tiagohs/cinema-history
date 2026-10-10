package com.tiagohs.domain.views

import com.tiagohs.domain.views.configs.IView
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.main_topics.MainTopicItem

interface HomeView: IView {
    fun setupContentView()

    /**
     * Conteúdo da Home: as áreas do app (homecontent.json) e as eras da História do Cinema
     * (maintopics.json), que ganham destaque no topo.
     */
    fun bindHomeContent(homeContentList: List<HomeContentItem>, historyEras: List<MainTopicItem>)

    fun startLoading()
    fun hideLoading()
}
