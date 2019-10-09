package com.tiagohs.cinema_history.ui.configs

interface IView {

    fun isInternetConnected(): Boolean
    fun isAdded(): Boolean

    fun onError(ex: Throwable?, message: Int = 0)
    fun onError(ex: Throwable?, message: String)
}