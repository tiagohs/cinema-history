package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.ContentType

open class Content {

    @SerializedName("type")
    var type: ContentType = ContentType.TEXT
}