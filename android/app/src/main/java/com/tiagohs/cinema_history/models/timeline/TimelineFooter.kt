package com.tiagohs.cinema_history.models.timeline

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

class TimelineFooter(
    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null
): Serializable, Timeline()