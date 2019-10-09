package com.tiagohs.cinema_history.presenter.configs

import com.tiagohs.cinema_history.ui.configs.IView


interface IPresenter<V : IView> {

    fun onBindView(view: V)
    fun onUnbindView()
}