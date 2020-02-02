package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.GifImage
import java.io.Serializable

class ContentGif(

    @SerializedName("gif_image")
    var gifImage: GifImage,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable