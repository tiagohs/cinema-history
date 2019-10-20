package com.tiagohs.cinema_history.enums

import com.google.gson.annotations.SerializedName

enum class TextViewUrlType(
    val type: String
){
    @SerializedName("online")
    ONLINE("online"),

    @SerializedName("screen")
    SCREEN("screen")
}