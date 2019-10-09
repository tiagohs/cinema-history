package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName

data class ContentText(

    @SerializedName("content_text")
    var contentText: String?
): Content()