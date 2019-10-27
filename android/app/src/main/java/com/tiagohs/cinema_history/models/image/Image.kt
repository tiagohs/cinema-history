package com.tiagohs.cinema_history.models.image

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.ImageScaleType
import com.tiagohs.cinema_history.enums.ImageType
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
