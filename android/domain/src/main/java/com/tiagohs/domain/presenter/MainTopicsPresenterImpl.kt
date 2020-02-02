package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.MainTopicsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainTopicsPresenterImpl @Inject constructor(
    val localService: LocalService
): BasePresenter<MainTopicsView>(), MainTopicsPresenter {

    override fun onBindView(view: MainTopicsView) {
        super.onBindView(view)

        this.view?.setupArguments()
        this.view?.setupScreenTitle()
        this.view?.setupScreenLayout()
    }

    override fun fetchMainTopics(mainTopicsType: MainTopicsType?) {
        val mainTopicsType = mainTopicsType ?: return

        view?.startLoading()

        when (mainTopicsType) {
            MainTopicsType.HISTORY_CINEMA -> {
                add(localService.getMainTopics()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.hideLoading()
                        view?.bindMainTopics(it)
                    }, {
                        view?.onError(it, "Houve um erro inesperado, tente novamente.")
                        view?.hideLoading()
                    })
                )
            }
            MainTopicsType.MIL_MOVIES -> {
                add(localService.getMilMoviesMainTopics()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.hideLoading()
                        view?.bindMainTopics(it)
                    }, {
                        view?.onError(it, "Houve um erro inesperado, tente novamente.")
                        view?.hideLoading()
                    })
                )
            }
            MainTopicsType.TIMELINE -> {
                add(localService.getMainTopics()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.hideLoading()
                        view?.bindMainTopics(it)
                    }, {
                        view?.onError(it, "Houve um erro inesperado, tente novamente.")
                        view?.hideLoading()
                    })
                )
            }
            MainTopicsType.DIRECTORS -> {
                add(localService.getDirectorsMainTopics()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.hideLoading()
                        view?.bindMainTopics(it)
                    }, {
                        view?.onError(it, "Houve um erro inesperado, tente novamente.")
                        view?.hideLoading()
                    })
                )
            }
        }
    }
}