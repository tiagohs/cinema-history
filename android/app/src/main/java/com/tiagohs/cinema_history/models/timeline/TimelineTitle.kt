package com.tiagohs.cinema_history.models.timeline

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TimelineTitle(

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("page_title")
    val pageTitle: String,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null
): Timeline(), Serializable