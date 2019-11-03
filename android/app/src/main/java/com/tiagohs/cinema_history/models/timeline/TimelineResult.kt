package com.tiagohs.cinema_history.models.timeline

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TimelineResult(
    @SerializedName("id")
    val id: Int,

    @SerializedName("color")
    val color: String,

    @SerializedName("title_text_color")
    val titleTextColor: String,

    @SerializedName("timeline_list")
    val timelineList: List<Timeline>
): Serializable