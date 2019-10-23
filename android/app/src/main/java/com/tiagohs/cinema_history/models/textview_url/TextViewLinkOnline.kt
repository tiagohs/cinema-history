package com.tiagohs.cinema_history.models.textview_url

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TextViewLinkOnline(
    @SerializedName("url")
    val url: String
): TextViewLink(), Serializable