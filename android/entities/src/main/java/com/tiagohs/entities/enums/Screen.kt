package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName

enum class Screen(
    val screenName: String
) {
    @SerializedName("timeline")
    TIMELINE_SCREEN("timeline"),

    @SerializedName("link_online")
    LINK_ONLINE("link_online")
}