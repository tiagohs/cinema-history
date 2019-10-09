package com.tiagohs.cinema_history.enums

import android.content.res.AssetManager
import android.graphics.Typeface

enum class FontEnum(
    val fontName: String
) {
    OPEN_SANS_BOLD("OpenSans-Bold.ttf"),
    OPEN_SANS_ITALIC("OpenSans-Italic.ttf"),
    OPEN_SANS_LIGHT("OpenSans-Light.ttf"),
    OPEN_SANS_REGULAR("OpenSans-REgular.ttf"),
    OPEN_SANS_CONDENSED_BOLD("OpenSansCondensed-Bold.ttf"),
    OPEN_SANS_CONDENSED_LIGHT("OpenSansCondensed_Light.ttf"),
    OSWALD_BOLD("Oswald-Bold.ttf"),
    OSWALD_LIGHT("Oswald-Light.ttf"),
    OSWALD_REGULAR("Oswald-Regular.ttf"),
    ROBOTO_BOLD("Roboto-Bold.ttf"),
    ROBOTO_LIGHT("Roboto-Light.ttf"),
    ROBOTO_ITALIC("Roboto-Italic.ttf"),
    ROBOTO_MEDIUM("Roboto-Medium.ttf"),
    ROBOTO_REGULAR("Roboto-Regular.ttf"),
    ROBOTO_THIN("Roboto-Thin.ttf");

    val fontWithPath: String
        get() = String.format("fonts/%s", this.fontName)

    fun getTypeface(asset: AssetManager): Typeface {
        return Typeface.createFromAsset(asset, this.fontWithPath)
    }
}