package com.tiagohs.cinema_history.presentation.configs

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewbinding.ViewBinding
import com.tiagohs.cinema_history.App
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.dagger.AppComponent
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.helpers.edgetoedge.SystemBarsInsets
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ServerUtils
import com.tiagohs.uicomponents.alertsnack.AlertSnackBar

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
        private set

    abstract fun inflateBinding(inflater: LayoutInflater): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = inflateBinding(layoutInflater)
        setContentView(binding.root)

        SystemBarsInsets.apply(this)

        if (drawsBehindStatusBar()) StatusBarScrim.apply(binding.root)
    }

    protected fun drawsBehindStatusBar(): Boolean {
        val a = theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowTranslucentStatus))
        return try { a.getBoolean(0, false) } finally { a.recycle() }
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
        findViewById<View>(R.id.errorContainer)?.show()

        if (message != 0) {
            findViewById<TextView>(R.id.errorDescription)?.setResourceText(message)
        }

        findViewById<View>(R.id.tryAgainButton)?.setOnClickListener {
            onTryAgainClicked?.invoke()
        }
    }

    open fun hideError() {
        findViewById<View>(R.id.errorContainer)?.hide()
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


    abstract fun onGetMenuLayoutId(): Int
}