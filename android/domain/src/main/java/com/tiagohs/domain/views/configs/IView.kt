package com.tiagohs.domain.views.configs

interface IView {

    fun isInternetConnected(): Boolean
    fun isAdded(): Boolean

    fun showError(message: Int = 0, onTryAgainClicked: (() -> Unit)? = null)
    fun hideError()
    fun onError(ex: Throwable?, message: Int = 0)
    fun onError(ex: Throwable?, message: String)
}