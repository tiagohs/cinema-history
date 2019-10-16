package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName

data class ContentInformation(

    @SerializedName("contentText")
    var contentText: String,

    @SerializedName("contentTitle")
    var contentTitle: String,

    @SerializedName("source")
    var source: String? = null
) {
}