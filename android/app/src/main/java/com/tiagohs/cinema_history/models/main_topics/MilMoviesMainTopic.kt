package com.tiagohs.cinema_history.models.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

class MilMoviesMainTopic(
    @SerializedName("id")
    val id: Int,

    @SerializedName("list_id")
    val list_id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("background_color")
    val backgroundColor: String? = null,

    @SerializedName("title_color")
    val titleColor: String? = null,

    @SerializedName("image")
    val image: Image
): MainTopic() , Serializable