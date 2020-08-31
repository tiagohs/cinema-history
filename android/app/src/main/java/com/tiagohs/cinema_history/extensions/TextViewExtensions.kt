package com.tiagohs.cinema_history.extensions

import android.app.Activity
import android.content.Context
import android.widget.TextView
import com.google.gson.Gson
import com.tiagohs.cinema_history.presentation.activities.MovieDetailsActivity
import com.tiagohs.cinema_history.presentation.activities.PersonDetailsActivity
import com.tiagohs.entities.enums.TextViewLinkScreenType
import com.tiagohs.entities.enums.TextViewUrlType
import com.tiagohs.entities.textview_url.TextViewLinkOnline
import com.tiagohs.entities.textview_url.TextViewLinkScreen
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.startActivityWithSlideRightToLeftAnimation
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
                        else -> return@setOnLinkClickListener false
                    }

                    val activity = context as? Activity ?: return@setOnLinkClickListener false
                    activity.startActivityWithSlideRightToLeftAnimation(intent)

                    return@setOnLinkClickListener true
                }
            }

            true
        }
}
