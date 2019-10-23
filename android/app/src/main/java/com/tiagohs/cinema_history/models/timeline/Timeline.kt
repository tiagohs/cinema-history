package com.tiagohs.cinema_history.models.timeline

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.TimelineType
import java.io.Serializable

open class Timeline: Serializable {

    @SerializedName("type")
    var type: TimelineType = TimelineType.RIGHT

    @SerializedName("background_color")
    val backgroundColor: String? = null

    @SerializedName("background_transparent_color")
    val backgroundTransparentColor: String? = null
}