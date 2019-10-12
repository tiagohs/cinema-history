package com.tiagohs.cinema_history.enums

import android.os.Parcel
import android.os.Parcelable
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