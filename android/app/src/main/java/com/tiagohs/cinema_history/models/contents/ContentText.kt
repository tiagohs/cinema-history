package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ContentText(

    @SerializedName("content_text")
    var contentText: String,

    @SerializedName("font")
    var font: String?
): Content(), Serializable