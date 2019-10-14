package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.GifType
import com.tiagohs.cinema_history.models.image.GifImage
import java.io.Serializable

class ContentGif(

    @SerializedName("gif_image")
    var gifImage: GifImage,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable