package com.tiagohs.cinema_history.helpers.extensions

import android.app.Activity
import android.content.Context
import android.widget.TextView
import com.google.gson.Gson
import com.tiagohs.cinema_history.enums.TextViewLinkScreenType
import com.tiagohs.cinema_history.enums.TextViewUrlType
import com.tiagohs.cinema_history.enums.TimelineType
import com.tiagohs.cinema_history.models.textview_url.TextViewLink
import com.tiagohs.cinema_history.models.textview_url.TextViewLinkOnline
import com.tiagohs.cinema_history.models.textview_url.TextViewLinkScreen
import com.tiagohs.cinema_history.ui.activities.MovieDetailsActivity
import com.tiagohs.cinema_history.ui.activities.PersonDetailsActivity
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.json.JSONObject

fun TextView.setupLinkableTextView(globalContext: Context?) {
    BetterLinkMovementMethod
        .linkifyHtml(this)
        .setOnLinkClickListener { _, url ->
            val context = globalContext ?: return@setOnLinkClickListener true
            val value = url.split("://_")
            val jsonObject = JSONObject(value[1])
            val type = TextViewUrlType.getContentType(jsonObject.get("type").toString())

            when (type) {
                TextViewUrlType.ONLINE -> {
                    val obj = Gson().fromJson<TextViewLinkOnline>(value[1], TextViewLinkOnline::class.java)

                    context.openLink(obj.url)
                }
                TextViewUrlType.SCREEN -> {
                    val obj = Gson().fromJson<TextViewLinkScreen>(value[1], TextViewLinkScreen::class.java)
                    val intent = when (obj.screenType) {
                        TextViewLinkScreenType.MOVIE -> MovieDetailsActivity.newIntent(context, obj.id)
                        TextViewLinkScreenType.PERSON -> PersonDetailsActivity.newIntent(context, obj.id)
                    }

                    val activity = context as? Activity

                    if (activity != null) {
                        activity.startActivityWithSlideAnimation(intent)
                        return@setOnLinkClickListener true
                    }

                    context.startActivity(intent)
                }
            }

            true
        }
}