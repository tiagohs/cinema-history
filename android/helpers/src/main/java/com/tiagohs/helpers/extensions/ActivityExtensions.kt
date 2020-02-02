package com.tiagohs.helpers.extensions

import android.app.Activity
import android.content.Intent
import android.view.WindowManager
import androidx.core.content.ContextCompat
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

fun Activity.startActivityWithSlideAnimation(intent: Intent) {
    startActivity(intent)

    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}