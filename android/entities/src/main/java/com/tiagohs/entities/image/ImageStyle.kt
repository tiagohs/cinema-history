package com.tiagohs.entities.image

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageStyle(

    @SerializedName("width")
    var width: Int? = null,

    @SerializedName("height")
    var height: Int? = null,

    @SerializedName("resize")
    var resize: ImageResize? = null,

    @SerializedName("scale_type")
    var scaleType: String? = null
): Serializable