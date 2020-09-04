package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.TimelinePageView
import com.tiagohs.helpers.R
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
                view?.onError(it, R.string.error_unknown)
            })
        )
    }
}