package com.tiagohs.domain.presenter

import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.views.ReferenceView
import com.tiagohs.domain.views.TimelineView
import com.tiagohs.helpers.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ReferencePresenterImpl @Inject constructor(
    val localService: LocalService
): ReferencePresenter, BasePresenter<ReferenceView>() {

    override fun fetchReferences() {
        add(localService.getReferences()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.bindReference(it)
            }, {
                view?.onError(it, R.string.error_unknown)
                view?.hideLoading()
            })
        )
    }
}