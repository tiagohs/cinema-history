package com.tiagohs.cinema_history.models

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.TextViewUrlType


data class TextViewUrl(
    @SerializedName("type")
    val type: TextViewUrlType,

    @SerializedName("value")
    val value: String
)