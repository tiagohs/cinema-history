package com.tiagohs.entities.textview_url

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.TextViewUrlType


open class TextViewLink {

    @SerializedName("type")
    val type: TextViewUrlType = TextViewUrlType.ONLINE
}