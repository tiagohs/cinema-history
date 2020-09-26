package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName

enum class NomineeType(
    val type: String
) {
    @SerializedName("movie")
    MOVIE("movie"),

    @SerializedName("person")
    PERSON("person");

    companion object {
        fun getContentType(type: String): NomineeType {
            var typeEnum = MOVIE

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