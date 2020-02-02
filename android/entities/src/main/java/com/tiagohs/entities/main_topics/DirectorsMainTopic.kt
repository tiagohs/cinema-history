package com.tiagohs.entities.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.Image
import java.io.Serializable

class DirectorsMainTopic(
    @SerializedName("person_id")
    val personId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val image: Image
): MainTopic() , Serializable