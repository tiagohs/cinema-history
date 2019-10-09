package com.tiagohs.cinema_history.helpers.utils

import android.graphics.drawable.ColorDrawable
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
object ColorUtils {

    fun getPressedColorRippleDrawable(normalColor: Int, pressedColor: Int): RippleDrawable {
        return RippleDrawable(
            getPressedColorSelector(normalColor, pressedColor),
            getColorDrawableFromColor(normalColor),
            null
        )
    }

    fun getPressedColorSelector(normalColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf()
            ),
            intArrayOf(pressedColor, pressedColor, pressedColor, normalColor)
        )
    }

    fun getColorDrawableFromColor(color: Int): ColorDrawable {
        return ColorDrawable(color)
    }
}