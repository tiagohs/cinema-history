package com.tiagohs.entities.timeline

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.contents.ContentInformation
import com.tiagohs.entities.image.Image
import java.io.Serializable

data class TimelineItem(
    @SerializedName("year")
    val year: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("image_transparent")
    val imageTransparent: Boolean = false,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("margin_top")
    val marginTop: Int? = null,

    @SerializedName("image")
    val image: Image? = null,

    @SerializedName("image_info")
    val imageInfo: ContentInformation? = null
): Timeline(), Serializable