package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.HomeView
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomePresenterImpl
@Inject constructor(
    val localService: LocalService
): HomePresenter, BasePresenter<HomeView>() {

    override fun onBindView(view: HomeView) {
        super.onBindView(view)

        this.view?.let {
            it.startLoading()
            it.setupContentView()
        }
    }

    override fun fetchHomeContent() {
        // As eras são um complemento: se falharem, a Home continua mostrando as demais áreas.
        val eras = localService.getMainTopics()
            .map { topics -> historyEras(topics) }
            .onErrorReturn { emptyList() }

        add(Observable.zip(
                localService.getHomeContent(),
                eras,
                BiFunction<List<HomeContentItem>, List<MainTopicItem>, Pair<List<HomeContentItem>, List<MainTopicItem>>> { home, topics ->
                    home to topics
                })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (homeContent, historyEras) ->
                view?.hideLoading()
                view?.bindHomeContent(homeContent, historyEras)
            }, {
                view?.hideLoading()
                view?.onError(it, R.string.error_unknown)
            })
        )
    }

    private fun historyEras(topics: List<MainTopic>): List<MainTopicItem> =
        topics.filterIsInstance<MainTopicItem>()
            .filter { it.mainTopicType == null || it.mainTopicType == MainTopicsType.HISTORY_CINEMA }
}
