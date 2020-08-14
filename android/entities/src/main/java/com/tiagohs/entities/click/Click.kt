package com.tiagohs.entities.click

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.Screen

data class Click(
    @SerializedName("screen")
    var screen: Screen? = null,

    @SerializedName("video_id")
    val parameters: List<ScreenParameter>? = null
) {
}