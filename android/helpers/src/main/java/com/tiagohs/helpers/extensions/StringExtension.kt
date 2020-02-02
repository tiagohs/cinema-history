package com.tiagohs.helpers.extensions

import android.text.Spannable
import android.text.Spanned
import android.text.style.URLSpan
import androidx.core.text.HtmlCompat
import com.tiagohs.helpers.tools.URLSpanNoUnderline

fun String.styledString(): Spanned {
    val htmlString = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)

    val spannedText = Spannable.Factory.getInstance().newSpannable(htmlString)

    return removeUnderlines(spannedText)
}

fun removeUnderlines(p_Text: Spannable): Spannable {
    val spans = p_Text.getSpans(0, p_Text.length, URLSpan::class.java)

    for (span in spans) {
        val start = p_Text.getSpanStart(span)
        val end = p_Text.getSpanEnd(span)

        p_Text.removeSpan(span)
        p_Text.setSpan(URLSpanNoUnderline(span.url), start, end, 0)
    }

    return p_Text
}