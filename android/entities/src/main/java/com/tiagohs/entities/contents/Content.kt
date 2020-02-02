package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.ContentType
import java.io.Serializable

open class Content: Serializable {

    @SerializedName("type")
    var type: ContentType = ContentType.TEXT
}