package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.ContentType
import java.io.Serializable

open class Content: Serializable {

    @SerializedName("type")
    var type: ContentType = ContentType.TEXT
}