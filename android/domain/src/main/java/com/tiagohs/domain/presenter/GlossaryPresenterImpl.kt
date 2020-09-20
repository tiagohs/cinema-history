package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.views.GlossaryView
import com.tiagohs.entities.Glossary
import com.tiagohs.helpers.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class GlossaryPresenterImpl @Inject constructor(
    val localService: LocalService
) : GlossaryPresenter, BasePresenter<GlossaryView>() {

    override fun fetchPageContent() {
        view?.startLoading()

        add(localService.getGlossary()
            .map {
                val list = it.toMutableList()

                list.sortWith { p0, p1 -> p0?.name?.compareTo(p1.name) ?: 0 }

                return@map list
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.bindGlossaryContent(it)
            }, {
                view?.onError(it, R.string.error_unknown)
                view?.hideLoading()
            })
        )
    }
}