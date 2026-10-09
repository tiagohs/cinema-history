package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.AwardView
import com.tiagohs.entities.awards.AwardYearSummary
import com.tiagohs.entities.awards.NomineeResult
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AwardPresenterImpl @Inject constructor(
    val localService: LocalService
): AwardPresenter, BasePresenter<AwardView>() {

    /** Últimos anos abertos: ir e voltar entre anos não relê o JSON. */
    private val yearCache = object : LinkedHashMap<String, NomineeResult>(YEAR_CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, NomineeResult>?): Boolean =
            size > YEAR_CACHE_SIZE
    }

    private var yearRequest: Disposable? = null

    override fun onBindView(view: AwardView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun onUnbindView() {
        yearRequest = null

        super.onUnbindView()
    }

    override fun fetchAwardsNominees(awardMainTopic: AwardMainTopic?) {
        val awardId = awardMainTopic?.id ?: return

        view?.startLoading()

        add(Observable.zip(
            localService.fetchAwardsNomineesIndex(awardId).subscribeOn(Schedulers.io()),
            localService.fetchAwardsHistory(awardId).subscribeOn(Schedulers.io()).onErrorResumeNext { _: Throwable -> Observable.just<List<Content>>(emptyList()) },
            { yearIndex: List<AwardYearSummary>, awardsHistory: List<Content> ->
                awardMainTopic.yearIndex = yearIndex
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

    override fun fetchAwardYear(awardId: Int, year: String) {
        val key = "$awardId/$year"

        yearCache[key]?.let {
            cancelYearRequest()
            view?.bindAwardYear(year, it)
            return
        }

        // Só o último ano pedido interessa: troca rápida de ano cancela a leitura anterior.
        cancelYearRequest()
        yearRequest = localService.fetchAwardsNomineesYear(awardId, year)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                yearCache[key] = it
                view?.bindAwardYear(year, it)
            }, {
                view?.onAwardYearError(year, it)
            })
        yearRequest?.let { add(it) }
    }

    /** Remove do CompositeDisposable (que também cancela) para não acumular leituras antigas. */
    private fun cancelYearRequest() {
        yearRequest?.let { subscribers.remove(it) }
        yearRequest = null
    }

    companion object {
        private const val YEAR_CACHE_SIZE = 6
    }
}
