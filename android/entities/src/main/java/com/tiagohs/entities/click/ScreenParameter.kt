package com.tiagohs.entities.click

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ScreenParameter(
    @SerializedName("key")
    var key: String? = null,

    @SerializedName("value")
    val value: String
): Serializable