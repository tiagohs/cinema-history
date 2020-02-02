package com.tiagohs.entities.timeline

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.TimelineType
import java.io.Serializable

open class Timeline: Serializable {

    @SerializedName("type")
    var type: TimelineType = TimelineType.ITEM

}