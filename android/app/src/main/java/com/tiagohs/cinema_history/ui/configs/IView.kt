package com.tiagohs.cinema_history.ui.configs

import androidx.appcompat.widget.Toolbar

interface IView {

    fun isInternetConnected(): Boolean
    fun isAdded(): Boolean

    fun onError(ex: Throwable?, message: Int = 0)
    fun onError(ex: Throwable?, message: String)
}