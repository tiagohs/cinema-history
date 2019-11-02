package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.LocalService
import com.tiagohs.cinema_history.ui.views.TimelinePageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TimelinePagePresenterImpl @Inject constructor(
    val localService: LocalService
): TimelinePagePresenter, BasePresenter<TimelinePageView>() {

    override fun fetchTimelineItems() {
        add(localService.getTimelineItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.bindTimelineIDs(it)
            }, {
                view?.onError(it, "Houve um erro inesperado, tente novamente.")
            })
        )
    }
}