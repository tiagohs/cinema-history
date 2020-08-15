package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ReferenceType(
    val type: String
): Serializable {
    @SerializedName("media")
    MEDIA("media"),

    @SerializedName("text")
    TEXT("text");

    companion object {
        fun getContentType(type: String): ReferenceType {
            var typeEnum = MEDIA

            for (typeValue in values()) {
                if (typeValue.type == type) {
                    typeEnum = typeValue
                    break
                }
            }

            return typeEnum
        }
    }
}