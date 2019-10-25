package com.tiagohs.cinema_history.helpers.extensions

import android.app.Activity
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.tiagohs.cinema_history.R


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