package com.tiagohs.domain.presenter

import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.PresentationView
import com.tiagohs.helpers.R
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
                view?.onError(it, R.string.error_unknown)
            })
        )
    }

}
