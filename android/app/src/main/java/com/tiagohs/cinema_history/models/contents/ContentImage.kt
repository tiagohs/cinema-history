package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.GifType
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

data class ContentImage(

    @SerializedName("path")
    var image: Image,

    @SerializedName("height")
    var height: Int? = null,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable