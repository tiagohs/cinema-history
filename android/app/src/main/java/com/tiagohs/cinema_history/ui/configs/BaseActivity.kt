package com.tiagohs.cinema_history.ui.configs

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.tiagohs.cinema_history.App
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.dagger.AppComponent
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.helpers.extensions.toast
import com.tiagohs.cinema_history.helpers.utils.ServerUtils

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
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (onGetMenuLayoutId() != 0)
            menuInflater.inflate(onGetMenuLayoutId(), menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return false
        }
    }

    fun openUrl(url: String?) {

        if (!url.isNullOrEmpty()) {
            try {
                val urlUri = Uri.parse(url)
                val intent = CustomTabsIntent.Builder()
                        .setToolbarColor(getResourceColor(R.color.colorPrimary))
                        .setShowTitle(true)
                        .build()
                intent.launchUrl(this, urlUri)
            } catch (e: Exception) {
                toast(e.message)
            }
        }
    }

    protected fun startFragment(fragmentID: Int, fragment: Fragment) {
        val fm = supportFragmentManager
        val f = fm.findFragmentById(fragmentID)

        if (null == f) {
            fm.beginTransaction()
                    .add(fragmentID, fragment)
                    .commitAllowingStateLoss()
        } else {
            fm.beginTransaction()
                    .replace(fragmentID, fragment)
                    .commitAllowingStateLoss()
        }
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

    open fun onError(ex: Throwable?, message: Int) {

        val finalMessage = if (message == 0) {
            R.string.unknown_error
        } else {
            message
        }

        onError(ex, finalMessage)
    }

    open fun onError(ex: Throwable?, message: String) {
        toast(message)
    }

    abstract fun onGetLayoutViewId() : Int
    abstract fun onGetMenuLayoutId(): Int
}