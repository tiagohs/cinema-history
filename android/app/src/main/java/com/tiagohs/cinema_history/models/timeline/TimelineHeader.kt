package com.tiagohs.cinema_history.models.timeline

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

class TimelineHeader(
    @SerializedName("title")
    val title: String,

    @SerializedName("subtitle")
    val subtitle: String,

    @SerializedName("title_color")
    val titleColor: String,

    @SerializedName("subtitle_color")
    val subtitleColor: String,

    @SerializedName("image")
    val image: Image
): Serializable, Timeline() {
}