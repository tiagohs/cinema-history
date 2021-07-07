package com.tiagohs.cinema_history.presentation.configs

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.tiagohs.cinema_history.App
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.dagger.AppComponent
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ServerUtils
import com.tiagohs.uicomponents.alertsnack.AlertSnackBar
import kotlinx.android.synthetic.main.view_error.*

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(onGetLayoutViewId())
    }

    /*fun getConfiguratedAd(adView: AdView) {
        adView.loadAd(AdRequest.Builder().build())
    }*/

    fun setupToolbar(toolbar: Toolbar, displayHomeAsUpEnabled: Boolean = true, displayShowTitleEnabled: Boolean = false) {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)
        supportActionBar?.setDisplayShowTitleEnabled(displayShowTitleEnabled)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (onGetMenuLayoutId() != 0)
            menuInflater.inflate(onGetMenuLayoutId(), menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun getApplicationComponent(): AppComponent? {
        val application = application ?: return null

        return (application as App).appComponent
    }

    fun isInternetConnected(): Boolean {
        return ServerUtils.isNetworkConnected(this)
    }

    fun isAdded(): Boolean {
        return !isDestroyed
    }

    fun setScreenTitle(title: String?) {
        supportActionBar?.setTitle(title)
    }

    fun setScreenSubtitle(title: String?) {
        supportActionBar?.setSubtitle(title)
    }

    open fun showError(message: Int,
                       type: MessageViewType,
                       duration: Int,
                       onTryAgainClicked: (() -> Unit)?) {
        errorContainer.show()


        if (message != 0) {
            errorDescription.setResourceText(message)
        }

        tryAgainButton.setOnClickListener {
            onTryAgainClicked?.invoke()
        }
    }

    open fun hideError() {
        errorContainer.hide()
    }

    open fun onError(ex: Throwable?, message: Int, type: MessageViewType, duration: Int) {
        onError(ex, getResourceString(message), type, duration)
    }

    fun onError(ex: Throwable?, message: String, type: MessageViewType, duration: Int) {
        val coordanatorView = findViewById<CoordinatorLayout>(R.id.coordinator)

        if (coordanatorView != null) {
            AlertSnackBar.make(coordanatorView, type, getResourceString(R.string.error_title_ops), message, duration)
                ?.show()
            return
        }

        toast(message)
    }


    abstract fun onGetLayoutViewId() : Int
    abstract fun onGetMenuLayoutId(): Int
}