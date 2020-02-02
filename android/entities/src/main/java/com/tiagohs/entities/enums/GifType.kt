package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName

enum class GifType(
    val type: String
) {
    @SerializedName("url")
    GIF_URL("url"),

    @SerializedName("local")
    GIF_LOCAL("local")
}