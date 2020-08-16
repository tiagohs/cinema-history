package com.tiagohs.helpers.extensions

import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.TypedValue
import android.widget.TextView
import com.tiagohs.entities.enums.FontEnum
import java.util.concurrent.atomic.AtomicBoolean


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


fun TextView.setResourceFont(fontName: String?) {
    fontName ?: return
    val context = context ?: return

    typeface = context.getResourceFont(fontName)
}

fun TextView.setResourceFont(fontEnum: FontEnum?) {
    fontEnum ?: return

    setResourceFont(fontEnum.fontName)
}

fun TextView?.setResourceFontSize(fontSize: Int?) {
    if (this == null) {
        return
    }

    setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        context.resources.getDimension(fontSize ?: 16.convertIntToDp(context))
    )
}

fun TextView?.setResourceText(id: Int) {
    if (this == null) {
        return
    }

    text = context.getResourceString(id)
}

fun TextView?.setResourceStyledText(text: String?) {
    setResourceText(text?.styledString())
}


fun TextView?.setResourceText(text: CharSequence?) {
    if (this == null) {
        return
    }

    this.text = text ?: return
}

fun TextView?.setResourceTextColor(resName: String?) {
    this ?: return
    resName ?: return

    setTextColor(context.getResourceColor(resName))
}

fun TextView?.setResourceTextColor(color: Int) {
    if (this == null) {
        return
    }

    setTextColor(context.getResourceColor(color))
}

fun TextView?.setResourceTextSize(resId: Int) {
    if (this == null) {
        return
    }

    setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(resId))
}