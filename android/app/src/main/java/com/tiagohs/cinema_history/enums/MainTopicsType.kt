package com.tiagohs.cinema_history.enums

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

enum class MainTopicsType(
    val type: String
) {

    @SerializedName("history_cinema")
    HISTORY_CINEMA("history_cinema"),

    @SerializedName("mil_movies")
    MIL_MOVIES("mil_movies"),

    @SerializedName("timeline")
    TIMELINE("timeline");

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