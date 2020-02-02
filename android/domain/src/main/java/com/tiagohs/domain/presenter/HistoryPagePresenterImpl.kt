package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.HistoryPageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HistoryPagePresenterImpl @Inject constructor(
    val localService: LocalService
): HistoryPagePresenter, BasePresenter<HistoryPageView>() {

    override fun onBindView(view: HistoryPageView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchPageContent(mainTopicId: Int?, sumarioId: Int?) {
        val sumarioId = sumarioId ?: return
        val mainId = mainTopicId ?: return

        add(localService.getPage(mainId, sumarioId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.bindPageContent(it)
            }, {

            })
        )
    }
}