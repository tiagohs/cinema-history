package com.tiagohs.entities.timeline

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TimelineFooter(
    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null
): Serializable, Timeline()