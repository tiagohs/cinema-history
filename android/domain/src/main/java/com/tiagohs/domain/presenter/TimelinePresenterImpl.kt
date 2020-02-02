package com.tiagohs.domain.presenter

import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.views.TimelineView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TimelinePresenterImpl @Inject constructor(
    val localService: LocalService
): TimelinePresenter, BasePresenter<TimelineView>() {

    override fun onBindView(view: TimelineView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchTimeline(id: Int) {
        add(localService.getTimeline(id)
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