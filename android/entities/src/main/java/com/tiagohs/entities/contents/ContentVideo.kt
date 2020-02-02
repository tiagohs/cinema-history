package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ContentVideo(

    @SerializedName("video_id")
    val videoId: String,

    @SerializedName("height")
    val height: Int? = null,

    @SerializedName("information")
    val information: ContentInformation
): Content(), Serializable