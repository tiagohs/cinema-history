package com.tiagohs.entities.textview_url

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.TextViewLinkScreenType
import java.io.Serializable

class TextViewLinkScreen(

    @SerializedName("screen_type")
    val screenType: TextViewLinkScreenType = TextViewLinkScreenType.MOVIE,

    @SerializedName("id")
    val id: Int
): TextViewLink(), Serializable