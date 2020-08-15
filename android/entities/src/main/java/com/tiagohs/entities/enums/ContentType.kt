package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ContentType(
    val type: String
): Serializable {
    @SerializedName("text")
    TEXT("text"),

    @SerializedName("gif")
    GIF("gif"),

    @SerializedName("video")
    VIDEO("video"),

    @SerializedName("slide")
    SLIDE("slide"),

    @SerializedName("image")
    IMAGE("image"),

    @SerializedName("audio_stream")
    AUDIO_STREAM("audio_stream"),

    @SerializedName("quote")
    QUOTE("quote"),

    @SerializedName("block_special")
    BLOCK_SPECIAL("block_special");

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