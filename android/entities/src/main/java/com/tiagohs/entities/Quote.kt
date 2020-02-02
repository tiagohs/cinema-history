package com.tiagohs.entities

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.main_topics.MainTopic
import java.io.Serializable

class Quote(

    @SerializedName("id")
    val id: Int,

    @SerializedName("quote")
    val quote: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("icon_color")
    val iconColor: String? = null,

    @SerializedName("background_color")
    val backgroundColor: String? = null,

    @SerializedName("text_color")
    val textColor: String? = null
): MainTopic(), Serializable