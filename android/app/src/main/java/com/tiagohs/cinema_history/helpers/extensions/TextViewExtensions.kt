package com.tiagohs.cinema_history.helpers.extensions

import android.widget.TextView
import kotlinx.android.synthetic.main.adapter_page_text.view.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod

fun TextView.setupLinkableTextView() {
    BetterLinkMovementMethod
        .linkifyHtml(this)
        .setOnLinkClickListener { _, url ->
            context.openLink(url)
            true
        }
}