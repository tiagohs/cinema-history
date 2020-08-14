package com.tiagohs.entities.click

import com.google.gson.annotations.SerializedName

class ScreenParameter(
    @SerializedName("key")
    var key: String? = null,

    @SerializedName("value")
    val value: String
) {
}