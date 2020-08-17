package com.tiagohs.domain.views.configs

import com.google.android.material.snackbar.Snackbar
import com.tiagohs.entities.enums.MessageViewType

interface IView {

    fun isInternetConnected(): Boolean
    fun isAdded(): Boolean

    fun showError(message: Int = 0, type: MessageViewType = MessageViewType.ERROR, duration: Int = Snackbar.LENGTH_LONG,  onTryAgainClicked: (() -> Unit)? = null)
    fun hideError()
    fun onError(ex: Throwable?, message: Int = 0, type: MessageViewType = MessageViewType.ERROR, duration: Int = Snackbar.LENGTH_LONG)
    fun onError(ex: Throwable?, message: String, type: MessageViewType = MessageViewType.ERROR, duration: Int = Snackbar.LENGTH_LONG)
}