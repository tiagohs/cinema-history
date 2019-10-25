package com.tiagohs.cinema_history.helpers.extensions

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tiagohs.cinema_history.R


fun Context.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resource, duration).show()
}

fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text.orEmpty(), duration).show()
}

fun Context.hasPermission(permission: String)
        = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

@Suppress("DEPRECATION")
fun Context.getResourceColor(resource: Int): Int {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return getColor(resource)
    } else {
        return resources.getColor(resource)
    }
}

@Suppress("DEPRECATION")
fun Context.getResourceDrawable(resource: Int): Drawable? {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return getDrawable(resource)
    } else {
        return resources.getDrawable(resource)
    }
}

fun Context.openLink(url: String) {
    try {
        val urlUri = Uri.parse(url)
        val intent = CustomTabsIntent.Builder()
            .setToolbarColor(resources.getColor(R.color.colorPrimary))
            .setShowTitle(true)
            .build()
        intent.launchUrl(this, urlUri)
    } catch (e: Exception) {

    }
}

fun Context.getResourceColor(colorName: String): Int {
    val colorIdentifier = resources.getIdentifier(colorName, "color", packageName)

    return getResourceColor(colorIdentifier)
}