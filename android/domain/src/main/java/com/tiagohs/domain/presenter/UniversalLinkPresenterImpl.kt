package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.UniversalLinkView
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UniversalLinkPresenterImpl
@Inject constructor(
    val localService: LocalService
): UniversalLinkPresenter, BasePresenter<UniversalLinkView>() {

    override fun onBindView(view: UniversalLinkView) {
        super.onBindView(view)

        this.view?.let {
            it.setupContentFromUniversalLink()
        }
    }

    override fun fetchMainTopicById(mainTopicId: String?, itemSelectedPosition: String?) {

        add(localService.getMainTopics()
            .map { list -> list.find { (it as? MainTopicItem)?.id == mainTopicId?.toInt() } as? MainTopicItem ?: list.first() as MainTopicItem }
            .map {
                it.sumarioList = localService.getSumarioByMainTopicID(it.id).blockingFirst()

                it
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.startHistoryPage(it, itemSelectedPosition?.toIntOrNull() ?: 0)
            }, {
                view?.startHomeActivity()
            })
        )
    }
}