package com.tiagohs.cinema_history.models.image

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageResize(

    @SerializedName("width")
    val width: Int? = null,

    @SerializedName("height")
    val height: Int? = null

): Serializable