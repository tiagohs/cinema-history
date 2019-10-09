package com.tiagohs.cinema_history.helpers.extensions

import android.content.Context
import android.util.TypedValue


fun Int.convertIntToDp(context: Context?): Int {
    val resources = context?.resources ?: return 0
    val intDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)

    return intDp.toInt()
}