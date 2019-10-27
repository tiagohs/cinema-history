package com.tiagohs.cinema_history.models.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.Quote
import com.tiagohs.cinema_history.models.Sumario
import com.tiagohs.cinema_history.models.image.Image
import java.io.Serializable

data class MainTopicItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("subtitle")
    val subtitle: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("image")
    val image: Image,

    @SerializedName("title_color")
    val titleColor: String? = null,

    @SerializedName("title_background_color")
    val titleBackgroundColor: String? = null,

    @SerializedName("quote")
    val quote: Quote,

    @SerializedName("sumario_list")
    var sumarioList: List<Sumario>

): MainTopic(), Serializable {

}