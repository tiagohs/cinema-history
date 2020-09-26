package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName

enum class NomineeType(
    val type: String
) {
    @SerializedName("movie")
    MOVIE("movie"),

    @SerializedName("person")
    PERSON("person");
}