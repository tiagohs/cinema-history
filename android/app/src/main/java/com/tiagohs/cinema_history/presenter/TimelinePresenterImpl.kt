package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.LocalService
import com.tiagohs.cinema_history.ui.views.TimelineView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TimelinePresenterImpl @Inject constructor(
    val localService: LocalService
): TimelinePresenter, BasePresenter<TimelineView>() {

    override fun fetchTimelineItems() {
        add(localService.getTimeline()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.bindTimeline(it)
            }, {
                view?.onError(it, "Houve um erro inesperado, tente novamente.")
                view?.hideLoading()
            })
        )
    }
}