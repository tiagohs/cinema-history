package com.tiagohs.cinema_history.enums

import android.content.res.AssetManager
import android.graphics.Typeface

enum class FontEnum(
    val fontName: String
) {
    OPEN_SANS_BOLD("opensans_bold.ttf"),
    OPEN_SANS_ITALIC("opensans_italic.ttf"),
    OPEN_SANS_LIGHT("opensans_light.ttf"),
    OPEN_SANS_REGULAR("opensans_regular.ttf"),
    OPEN_SANS_CONDENSED_BOLD("opensanssondensed_bold.ttf"),
    OPEN_SANS_CONDENSED_LIGHT("opensanssondensed_light.ttf"),
    OSWALD_BOLD("oswald_bold.ttf"),
    OSWALD_LIGHT("oswald_light.ttf"),
    OSWALD_REGULAR("oswald_regular.ttf"),
    ROBOTO_BOLD("roboto_bold.ttf"),
    ROBOTO_LIGHT("roboto_light.ttf"),
    ROBOTO_ITALIC("roboto_italic.ttf"),
    ROBOTO_MEDIUM("roboto_medium.ttf"),
    ROBOTO_REGULAR("roboto_regular.ttf"),
    ROBOTO_THIN("roboto_thin.ttf"),
    BIG_SHOULDERS_DISPLAY_BOLD("bigshouldersdisplay_bold.ttf"),
    BIG_SHOULDERS_DISPLAY_LIGHT("bigshouldersdisplay_light.ttf"),
    BIG_SHOULDERS_DISPLAY_MEDIUM("bigshouldersdisplay_medium.ttf"),
    BIG_SHOULDERS_DISPLAY_REGULAR("bigshouldersdisplay_regular.ttf"),
    BIG_SHOULDERS_DISPLAY_THIN("bigshouldersdisplay_thin.ttf"),
    HEPTASLAB_BOLD("heptaslab_bold.ttf"),
    HEPTASLAB_LIGHT("heptaslab_light.ttf"),
    HEPTASLAB_MEDIUM("heptaslab_medium.ttf"),
    HEPTASLAB_REGULAR("heptaslab_regular.ttf"),
    HEPTASLAB_THIN("heptaslab_thin.ttf"),
    BILLIONAIRE_MEDIUM_GRUNGE("billionairemediumgrunge.ttf");

    val fontWithPath: String
        get() = String.format("fonts/%s", this.fontName)

    fun getTypeface(asset: AssetManager): Typeface {
        return Typeface.createFromAsset(asset, this.fontWithPath)
    }
}