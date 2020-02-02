package com.tiagohs.helpers.utils

import android.graphics.drawable.ColorDrawable
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import com.tiagohs.entities.ColorAsset

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

    fun getRandomColorAssets(): com.tiagohs.entities.ColorAsset {
        val listOfColors = listOf(
            com.tiagohs.entities.ColorAsset("red", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("pink", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("purple", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("deep_purple", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("blue", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("light_blue", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("cyan", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("teal", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("green", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("light_green", "md_black_1000"),
            com.tiagohs.entities.ColorAsset("lime", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("yellow", "md_black_1000"),
            com.tiagohs.entities.ColorAsset("amber", "md_black_1000"),
            com.tiagohs.entities.ColorAsset("orange", "md_black_1000"),
            com.tiagohs.entities.ColorAsset("deep_orange", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("brown", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("grey", "md_white_1000"),
            com.tiagohs.entities.ColorAsset("blue_grey", "md_white_1000")
        )

        return listOfColors.random()
    }
}


