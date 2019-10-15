package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.LocalService
import com.tiagohs.cinema_history.ui.views.PresentationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PresentationPresenterImpl @Inject constructor(
    val localService: LocalService
): PresentationPresenter, BasePresenter<PresentationView>() {

    override fun onBindView(view: PresentationView) {
        super.onBindView(view)

        this.view?.setupArguments()
        this.view?.bindSumarioHeader()
    }

    override fun fetchMoviesByListId(mainTopic: MainTopicItem?) {
        mainTopic ?: return

        add(localService.getSumarioByMainTopicID(mainTopic.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mainTopic.sumarioList = it

                view?.bindMainTopicPresentation(mainTopic)
            }, {

            })
        )
    }

}
