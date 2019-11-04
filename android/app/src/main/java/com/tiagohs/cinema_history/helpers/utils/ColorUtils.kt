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

    fun getRandomColorAssets(): ColorAsset {
        val listOfColors = listOf(
            ColorAsset("red", "md_white_1000"),
            ColorAsset("pink", "md_white_1000"),
            ColorAsset("purple", "md_white_1000"),
            ColorAsset("deep_purple", "md_white_1000"),
            ColorAsset("blue", "md_white_1000"),
            ColorAsset("light_blue", "md_white_1000"),
            ColorAsset("cyan", "md_white_1000"),
            ColorAsset("teal", "md_white_1000"),
            ColorAsset("green", "md_white_1000"),
            ColorAsset("light_green", "md_black_1000"),
            ColorAsset("lime", "md_white_1000"),
            ColorAsset("yellow", "md_black_1000"),
            ColorAsset("amber", "md_black_1000"),
            ColorAsset("orange", "md_black_1000"),
            ColorAsset("deep_orange", "md_white_1000"),
            ColorAsset("brown", "md_white_1000"),
            ColorAsset("grey", "md_white_1000"),
            ColorAsset("blue_grey", "md_white_1000")
        )

        return listOfColors.random()
    }
}

data class ColorAsset(
    val colorName: String,
    val textColorName: String
)

