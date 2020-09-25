package com.tiagohs.entities.enums

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
    ROBOTO_MONO_BOLD("robotomono_bold.ttf"),
    ROBOTO_MONO_ITALIC("robotomono_italic.ttf"),
    ROBOTO_MONO_LIGHT("robotomono_light.ttf"),
    ROBOTO_MONO_MEDIUM("robotomono_medium.ttf"),
    ROBOTO_MONO_REGULAR("robotomono_regular.ttf"),
    ROBOTO_MONO_THIN("robotomono_thin.ttf"),
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
    BILLIONAIRE_MEDIUM_GRUNGE("billionairemediumgrunge.ttf"),
    JOMOLHARI_REGULAR("jomolhari_regular.ttf"),
    MONTSERRAT_BOLD("montserrat_bold.ttf"),
    MONTSERRAT_ITALIC("montserrat_italic.ttf"),
    MONTSERRAT_LIGHT("montserrat_light.ttf"),
    MONTSERRAT_MEDIUM("montserrat_medium.ttf"),
    MONTSERRAT_REGULAR("montserrat_regular.ttf"),
    MONTSERRAT_THIN("montserrat_thin.ttf"),
    FUTURA_CONDESED_LIGHT("futura_condensed_light.otf"),
    FUTURA_MEDIUM_ITALIC("futura_medium_italic.ttf"),
    FUTURA_MEDIUM_CONDESDE_BT("futura_medium_condensed_bt.ttf"),
    FUTURA_MEDIUM_BT("futura_medium_bt.ttf"),
    FUTURA_LIGHT_ITALIC_FONT("futura_light_italic_font.ttf"),
    FUTURA_LIGHT_FONT("futura_light_font.ttf"),
    FUTURA_LIGHT_BT("futura_light_bt.ttf"),
    FUTURA_HEAVY_ITALIC_FONT("futura_heavy_italic_font.ttf"),
    FUTURA_HEAVY_FONT("futura_heavy_font.ttf"),
    FUTURA_EXTRA_BLACK_FONT("futura_extra_black_font.ttf"),
    FUTURA_BOOK_ITALIC_FONT("futura_book_italic_font.ttf"),
    FUTURA_BOOK_FONT("futura_book_font.ttf"),
    FUTURA_BOLD_ITALIC_FONT("futura_bold_italic_font.ttf"),
    FUTURA_BOLD_FONT("futura_bold_font.ttf");

    val fontWithPath: String
        get() = String.format("fonts/%s", this.fontName)

    fun getTypeface(asset: AssetManager): Typeface {
        return Typeface.createFromAsset(asset, this.fontWithPath)
    }
}