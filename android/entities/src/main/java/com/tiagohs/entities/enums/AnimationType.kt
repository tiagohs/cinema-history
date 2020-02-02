package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class AnimationType(
    val type: String
): Serializable {

    @SerializedName("shake_vertical")
    SHAKE_VERTICAL("shake_vertical"),

    @SerializedName("blink")
    BLINK_VERTICAL("blink");

}