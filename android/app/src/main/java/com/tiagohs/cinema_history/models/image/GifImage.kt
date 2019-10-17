package com.tiagohs.cinema_history.models.image

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.ImageType
import java.io.Serializable

data class GifImage(

    @SerializedName("image_type")
    val imageType: ImageType = ImageType.ONLINE,

    @SerializedName("url")
    val url: String,

    @SerializedName("thumbnail")
    val thumbnail: Image,

    @SerializedName("style")
    val imageStyle: ImageStyle? = null
): Serializable