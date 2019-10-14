package com.tiagohs.cinema_history.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ImageType(
    val type: String
): Serializable {

    @SerializedName("local")
    LOCAL("local"),

    @SerializedName("online")
    ONLINE("online");

}