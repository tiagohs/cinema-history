package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.GifType

class ContentGif(

    @SerializedName("gif_type")
    var gifType: GifType = GifType.GIF_LOCAL,

    @SerializedName("path")
    var path: String
): Content()