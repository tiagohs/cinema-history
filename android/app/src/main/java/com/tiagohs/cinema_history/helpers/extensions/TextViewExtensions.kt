package com.tiagohs.cinema_history.helpers.extensions

import android.widget.TextView
import com.google.gson.Gson
import com.tiagohs.cinema_history.enums.TextViewUrlType
import com.tiagohs.cinema_history.models.TextViewUrl
import me.saket.bettermovementmethod.BetterLinkMovementMethod

fun TextView.setupLinkableTextView() {
    BetterLinkMovementMethod
        .linkifyHtml(this)
        .setOnLinkClickListener { _, url ->
            val value = url.split("://_")
            val obj = Gson().fromJson<TextViewUrl>(value[1], TextViewUrl::class.java)

            when (obj.type) {
                TextViewUrlType.ONLINE -> {
                    context.openLink(obj.value)
                }
                TextViewUrlType.SCREEN -> {

                }
            }

            true
        }

}