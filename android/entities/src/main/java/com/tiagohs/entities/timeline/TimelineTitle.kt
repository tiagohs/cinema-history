package com.tiagohs.entities.timeline

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

    @SerializedName("coming_soon")
    val comingSoon: Boolean? = null,

    @SerializedName("previous")
    val previous: String? = null
): Timeline(), Serializable