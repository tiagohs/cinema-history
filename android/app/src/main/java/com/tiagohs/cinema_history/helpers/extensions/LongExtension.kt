package com.tiagohs.cinema_history.helpers.extensions

import android.icu.text.NumberFormat
import java.util.*

fun Long.toCurrency(): String {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        return format.format(this)
    } else {
        val formatter = java.text.NumberFormat.getCurrencyInstance(Locale.US)
        return formatter.format(this)
    }
}