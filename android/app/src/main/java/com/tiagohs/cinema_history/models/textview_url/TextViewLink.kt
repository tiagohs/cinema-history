package com.tiagohs.cinema_history.models.textview_url

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.TextViewUrlType


open class TextViewLink {

    @SerializedName("type")
    val type: TextViewUrlType = TextViewUrlType.ONLINE
}