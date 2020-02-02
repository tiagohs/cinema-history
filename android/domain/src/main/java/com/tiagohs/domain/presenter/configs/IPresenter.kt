package com.tiagohs.domain.presenter.configs

import com.tiagohs.domain.views.configs.IView


interface IPresenter<V : IView> {

    fun onBindView(view: V)
    fun onUnbindView()
}