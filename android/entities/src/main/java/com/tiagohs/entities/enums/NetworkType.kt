package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.R
import java.io.Serializable

enum class NetworkType(
    val type: String,
    val networkName: Int? = null,
    val color: String,
    val textColor: String
) : Serializable {

    @SerializedName("prime_video")
    PRIME_VIDEO("prime_video", R.string.prime_video,"prime_video_color", "md_white_1000"),

    @SerializedName("youtube")
    YOUTUBE("youtube", R.string.youtube, "youtube_color", "md_white_1000"),

    @SerializedName("kanopy")
    KANOPY("kanopy", R.string.kanopy, "kanopy_color", "md_white_1000"),

    @SerializedName("apple_tv")
    APPLE_TV("apple_tv", R.string.apple_tv, "apple_tv_color", "md_white_1000"),

    @SerializedName("amazon_video")
    AMAZON_VIDEO("amazon_video", R.string.amazon_video, "md_black_1000", "amazon_color"),

    @SerializedName("hbo_max")
    HBO_MAX("hbo_max", R.string.hbo_max, "hbo_max", "md_white_1000"),

    @SerializedName("unknown")
    UNKNOWN("unknown", color = "md_black_1000", textColor =  "md_white_1000");

    companion object {
        fun getContentType(type: String): NetworkType {
            var typeEnum = UNKNOWN

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