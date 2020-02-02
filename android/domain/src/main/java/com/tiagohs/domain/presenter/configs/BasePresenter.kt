package com.tiagohs.domain.presenter.configs

import com.tiagohs.domain.views.configs.IView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<V : IView>() : IPresenter<V> {

    protected var view: V? = null
    protected var subscribers: CompositeDisposable = CompositeDisposable()

    override fun onBindView(view: V) {
        this.view = view
    }

    override fun onUnbindView() {

        if (view != null)
            view = null

        subscribers.clear()
    }

    fun add(disposable: Disposable) {
        subscribers.add(disposable)
    }

}
