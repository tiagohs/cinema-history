package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ContentInformation(

    @SerializedName("contentText")
    var contentText: String? = null,

    @SerializedName("contentTitle")
    var contentTitle: String? = null,

    @SerializedName("source")
    var source: String? = null
): Serializable {
}