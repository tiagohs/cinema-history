package com.tiagohs.entities.image

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageResize(

    @SerializedName("width")
    var width: Int? = null,

    @SerializedName("height")
    var height: Int? = null

): Serializable