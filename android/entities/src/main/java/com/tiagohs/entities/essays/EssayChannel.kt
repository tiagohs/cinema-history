package com.tiagohs.entities.essays

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class EssayChannel(
    @SerializedName("imagePath")
    var imagePath: String,

    @SerializedName("url")
    var url: String,

    @SerializedName("name")
    var name: String
): Serializable