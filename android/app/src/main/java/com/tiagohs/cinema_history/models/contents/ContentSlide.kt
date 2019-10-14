package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

class ContentSlide(

    @SerializedName("images")
    var images: List<Image>,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable