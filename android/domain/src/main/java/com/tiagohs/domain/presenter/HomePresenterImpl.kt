package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.HistoryPageView
import com.tiagohs.domain.views.HomeView
import com.tiagohs.helpers.R
import io.reactivex.android.schedulers.AndroidSchedulers
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
        add(localService.getHomeContent()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.bindHomeContent(it)
            }, {
                view?.hideLoading()
                view?.onError(it, R.string.error_unknown)
            })
        )
    }
}