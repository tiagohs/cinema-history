package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName

enum class MainTopicsType(
    val type: String
) {

    @SerializedName("history_cinema")
    HISTORY_CINEMA("history_cinema"),

    @SerializedName("mil_movies")
    MIL_MOVIES("mil_movies"),

    @SerializedName("timeline")
    TIMELINE("timeline"),

    @SerializedName("directors")
    DIRECTORS("directors");

    companion object {
        fun getContentType(type: String): MainTopicsType {
            var typeEnum = HISTORY_CINEMA

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