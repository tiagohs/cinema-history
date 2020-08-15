package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class TimelineType(
    val type: String
): Serializable {
    @SerializedName("title")
    TITLE("title"),

    @SerializedName("footer")
    FOOTER("footer"),

    @SerializedName("item")
    ITEM("item");

    companion object {

        fun getContentType(type: String): TimelineType {
            var typeEnum = TITLE

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