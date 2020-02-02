package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ImageType(
    val type: String
): Serializable {

    @SerializedName("local")
    LOCAL("local"),

    @SerializedName("online")
    ONLINE("online"),

    @SerializedName("online_firebase")
    ONLINE_FIREBASE("online_firebase");

}