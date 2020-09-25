package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName

enum class SocialType(
    val screenName: String
) {
    @SerializedName("youtube")
    YOUTUBE("youtube"),

    @SerializedName("facebook")
    FACEBOOK("facebook"),

    @SerializedName("twitter")
    TWITTER("twitter"),

    @SerializedName("instagram")
    INSTAGRAM("instagram"),

    @SerializedName("site")
    SITE("site");
}