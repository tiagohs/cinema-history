package com.tiagohs.entities.image

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.ImageType
import java.io.Serializable

class Image(

    @SerializedName("image_type")
    val imageType: ImageType = ImageType.ONLINE,

    @SerializedName("url")
    val url: String,

    @SerializedName("animation")
    val animation: Animation? = null,

    @SerializedName("style")
    val imageStyle: ImageStyle? = null
): Serializable
