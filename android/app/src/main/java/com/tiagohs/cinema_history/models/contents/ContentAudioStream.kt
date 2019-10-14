package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

data class ContentAudioStream(

    @SerializedName("path")
    var path: String,

    @SerializedName("image")
    var image: Image,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable