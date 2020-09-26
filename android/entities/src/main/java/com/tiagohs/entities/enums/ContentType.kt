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
    BLOCK_SPECIAL("block_special"),

    @SerializedName("link_screen")
    LINK_SCREEN("link_screen"),

    @SerializedName("movie_list")
    MOVIE_LIST("movie_list"),

    @SerializedName("person_list")
    PERSON_LIST("person_list"),

    @SerializedName("recomendations")
    RECOMENDATIONS("recomendations"),

    @SerializedName("awards_nominees")
    AWARDS_NOMINEES("awards_nominees"),

    @SerializedName("movie_list_special")
    MOVIE_LIST_SPECIAL("movie_list_special");

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