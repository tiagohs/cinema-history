package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ContentText(

    @SerializedName("content_text")
    var contentText: String,

    @SerializedName("content_title")
    var contentTitle: String? = null,

    @SerializedName("font")
    var font: String?
): Content(), Serializable