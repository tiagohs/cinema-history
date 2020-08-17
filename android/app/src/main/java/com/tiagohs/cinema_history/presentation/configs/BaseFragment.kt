package com.tiagohs.cinema_history.presentation.configs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tiagohs.cinema_history.dagger.AppComponent
import com.tiagohs.entities.enums.MessageViewType

abstract class BaseFragment : Fragment() {

    abstract fun getViewID(): Int
    abstract fun onErrorAction()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getViewID(), container, false)
        return view
    }

    protected fun getApplicationComponent(): AppComponent? {
        val activity = activity ?: return null
        return (activity as BaseActivity).getApplicationComponent()
    }

    fun isInternetConnected(): Boolean = (activity as? BaseActivity)?.isInternetConnected() ?: false

    open fun onError(ex: Throwable?, message: Int, type: MessageViewType, duration: Int) {
        (activity as? BaseActivity)?.onError(ex, message, type, duration)
    }

    open fun onError(ex: Throwable?, message: String, type: MessageViewType, duration: Int) {
        (activity as? BaseActivity)?.onError(ex, message, type, duration)
    }

    open fun showError(message: Int,
                       type: MessageViewType,
                       duration: Int,
                       onTryAgainClicked: (() -> Unit)?) {
        (activity as? BaseActivity)?.showError(message, type, duration, onTryAgainClicked)
    }

    open fun hideError() {
        (activity as? BaseActivity)?.hideError()
    }

    /*fun getConfiguratedAd(adView: AdView) {
        val activity = activity ?: return

        return (activity as BaseActivity).getConfiguratedAd(adView)
    }*/

}