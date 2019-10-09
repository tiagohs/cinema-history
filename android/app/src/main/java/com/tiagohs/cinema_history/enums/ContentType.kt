package com.tiagohs.cinema_history.enums

import com.google.gson.annotations.SerializedName

enum class ContentType(
    val type: String
) {
    @SerializedName("text")
    TEXT("text"),

    @SerializedName("gif")
    GIF("gif");

    companion object {
        fun getContentType(type: String): ContentType {
            var typeEnum = TEXT

            for (typeValue in values()) {
                if (typeValue.type.equals(type)) {
                    typeEnum = typeValue
                    break
                }
            }

            return typeEnum
        }
    }
}