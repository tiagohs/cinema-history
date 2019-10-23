package com.tiagohs.cinema_history.models.textview_url

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.TextViewLinkScreenType
import java.io.Serializable

class TextViewLinkScreen(

    @SerializedName("screen_type")
    val screenType: TextViewLinkScreenType = TextViewLinkScreenType.MOVIE,

    @SerializedName("id")
    val id: Int
): TextViewLink(), Serializable