package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.Image
import java.io.Serializable

data class ContentImage(

    @SerializedName("image")
    var image: Image,

    @SerializedName("height")
    var height: Int? = null,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable