package com.tiagohs.entities.image

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.ImageType
import java.io.Serializable

class Image(

    @SerializedName("image_type")
    val imageType: ImageType = ImageType.ONLINE,

    @SerializedName("url")
    val url: String,

    @SerializedName("content_description")
    var contentDescription: String? = null,

    @SerializedName("animation")
    val animation: Animation? = null,

    @SerializedName("style")
    var imageStyle: ImageStyle? = null
): Serializable
