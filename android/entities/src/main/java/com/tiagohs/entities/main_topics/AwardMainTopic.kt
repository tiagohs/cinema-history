package com.tiagohs.entities.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.awards.Social
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.image.Image
import java.io.Serializable

data class AwardMainTopic(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("presented_by")
    val presentedBy: String? = null,

    @SerializedName("first_awarded_date")
    val firstAwardedDate: String? = null,

    @SerializedName("image")
    val image: Image,

    @SerializedName("nominees_id")
    val nominees_id: Int? = null,

    @SerializedName("history")
    val history: List<Content>? = null,

    @SerializedName("social_list")
    val socialList: List<Social>? = null,
): MainTopic() , Serializable