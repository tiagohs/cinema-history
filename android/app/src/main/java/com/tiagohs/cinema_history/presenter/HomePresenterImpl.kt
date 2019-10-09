package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.LocalService
import com.tiagohs.cinema_history.ui.views.HomeView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomePresenterImpl @Inject constructor(
    val localService: LocalService
):
    BasePresenter<HomeView>(),
    HomePresenter {

    override fun fetchMainTopics() {
        add(localService.getMainTopics()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.bindMainTopics(it)
            }, {

            })
        )
    }
}