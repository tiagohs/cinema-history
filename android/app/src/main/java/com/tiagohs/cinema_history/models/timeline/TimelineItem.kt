package com.tiagohs.cinema_history.models.timeline

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.contents.ContentInformation
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

data class TimelineItem(
    @SerializedName("year")
    val year: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("background_color")
    val backgroundColor: String,

    @SerializedName("year_color")
    val yearColor: String,

    @SerializedName("image_transparent")
    val imageTransparent: Boolean = false,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("image")
    val image: Image? = null,

    @SerializedName("image_info")
    val imageInfo: ContentInformation? = null
): Timeline(), Serializable