package com.tiagohs.cinema_history.helpers.extensions

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.TextView
import com.google.gson.Gson
import com.tiagohs.cinema_history.enums.TextViewLinkScreenType
import com.tiagohs.cinema_history.enums.TextViewUrlType
import com.tiagohs.cinema_history.models.textview_url.TextViewLinkOnline
import com.tiagohs.cinema_history.models.textview_url.TextViewLinkScreen
import com.tiagohs.cinema_history.ui.activities.MovieDetailsActivity
import com.tiagohs.cinema_history.ui.activities.PersonDetailsActivity
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean


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

fun TextView.justify() {
    val isJustify = AtomicBoolean(false)
    val textString = text.toString()
    val textPaint = paint
    val builder = SpannableStringBuilder()

    post {
        if (!isJustify.get()) {

            val lineCount = lineCount
            val textViewWidth = width

            for (i in 0 until lineCount) {

                val lineStart = layout.getLineStart(i)
                val lineEnd = layout.getLineEnd(i)

                val lineString = textString.substring(lineStart, lineEnd)

                if (i == lineCount - 1) {
                    builder.append(SpannableString(lineString))
                    break
                }

                val trimSpaceText = lineString.trim { it <= ' ' }
                val removeSpaceText = lineString.replace(" ".toRegex(), "")

                val removeSpaceWidth = textPaint.measureText(removeSpaceText)
                val spaceCount = (trimSpaceText.length - removeSpaceText.length).toFloat()

                val eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount

                val spannableString = SpannableString(lineString)
                for (j in trimSpaceText.indices) {
                    val c = trimSpaceText[j]
                    if (c == ' ') {
                        val drawable = ColorDrawable(0x00ffffff)
                        drawable.setBounds(0, 0, eachSpaceWidth.toInt(), 0)
                        val span = ImageSpan(drawable)
                        spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }

                builder.append(spannableString)
            }

            text = builder
            isJustify.set(true)
        }
    }
}