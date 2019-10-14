package com.tiagohs.cinema_history.helpers.extensions

import android.text.Spanned
import androidx.core.text.HtmlCompat

fun String.styledString(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}