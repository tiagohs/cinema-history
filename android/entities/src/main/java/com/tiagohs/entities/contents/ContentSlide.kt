package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.Image
import java.io.Serializable

class ContentSlide(

    @SerializedName("images")
    var images: List<Image>,

    @SerializedName("height")
    var height: Int? = null,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable