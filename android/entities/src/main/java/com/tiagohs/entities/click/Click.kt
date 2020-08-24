package com.tiagohs.entities.click

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.Screen

data class Click(
    @SerializedName("screen")
    var screen: Screen? = null,

    @SerializedName("button_text")
    var buttonText: String? = null,

    @SerializedName("parameters")
    val parameters: List<ScreenParameter>? = null
) {
}