package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class RatingType(
    val type: String
) : Serializable {
    @SerializedName("Internet Movie Database")
    INTERNET_MOVIE_DATABASE("Internet Movie Database"),

    @SerializedName("Rotten Tomatoes")
    TOMATOES("Rotten Tomatoes"),

    @SerializedName("Metacritic")
    METACRITIC("Metacritic"),

    @SerializedName("")
    UNKNOWN("");

    companion object {

        fun getContentType(type: String): RatingType {
            var typeEnum = UNKNOWN

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