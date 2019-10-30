package com.tiagohs.cinema_history.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ViewPosition(
    val position: String
): Serializable {
    @SerializedName("top_start")
    TOP_START("top_start"),

    @SerializedName("top_end")
    TOP_END("top_end"),

    @SerializedName("bottom_start")
    BOTTOM_START("bottom_start"),

    @SerializedName("bottom_end")
    BOTTOM_END("bottom_end");
}