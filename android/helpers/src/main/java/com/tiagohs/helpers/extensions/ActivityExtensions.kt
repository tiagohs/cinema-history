package com.tiagohs.helpers.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.view.WindowManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tiagohs.helpers.R


fun Activity.setScreenBackgroundColor(color: String) {
    setScreenBackgroundColor(applicationContext.getResourceColor(color))
}

fun Activity.setScreenBackgroundColor(color: Int) {
    val view = this.window.decorView

    view.setBackgroundColor(ContextCompat.getColor(this, color))
}

fun Activity.setStatusBarColor(color: Int) {
    val window = getWindow()

    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.setStatusBarColor(ContextCompat.getColor(this, color))
}

fun Activity.startActivityWithSlideRightToLeftAnimation(intent: Intent) {
    startActivity(intent)

    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

val Activity?.statusBarHeight: Int
    get() {
        val rectangle = Rect()

        this?.window?.decorView?.getWindowVisibleDisplayFrame(rectangle)

        return rectangle.top
    }

fun FragmentActivity.startFragment(fragmentID: Int, fragment: Fragment) {
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

fun Activity?.openLink(url: String?) {
    url ?: return
    this ?: return

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