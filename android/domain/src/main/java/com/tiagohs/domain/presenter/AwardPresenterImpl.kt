package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.AwardView
import com.tiagohs.entities.awards.NomineeResult
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AwardPresenterImpl @Inject constructor(
    val localService: LocalService
): AwardPresenter, BasePresenter<AwardView>() {

    override fun onBindView(view: AwardView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchAwardsNominees(awardMainTopic: AwardMainTopic?) {
        val awardId = awardMainTopic?.id ?: return

        view?.startLoading()

        add(Observable.zip(
            localService.fetchAwardsNominees(awardId).subscribeOn(Schedulers.io()),
            localService.fetchAwardsHistory(awardId).subscribeOn(Schedulers.io()).onErrorResumeNext { ex: Throwable -> return@onErrorResumeNext Observable.just<List<Content>>(emptyList()) },
            { awardsNominees: List<NomineeResult>, awardsHistory: List<Content> ->
                awardMainTopic.nomineesList = awardsNominees
                awardMainTopic.history = awardsHistory

                awardMainTopic
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.bindAwardsNomineesContent(it)
            }, {
                view?.hideLoading()
                view?.onError(it, R.string.error_unknown)
            })
        )
    }
}