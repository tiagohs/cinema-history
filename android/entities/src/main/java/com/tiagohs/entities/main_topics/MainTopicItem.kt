package com.tiagohs.entities.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.Quote
import com.tiagohs.entities.Sumario
import com.tiagohs.entities.enums.ViewPosition
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

    @SerializedName("blocked")
    val blocked: Boolean,

    @SerializedName("presentation_image")
    val presentationImage: Image? = null,

    @SerializedName("title_color")
    val titleColor: String? = null,

    @SerializedName("title_background_color")
    val titleBackgroundColor: String? = null,

    @SerializedName("quote_position")
    val quotePosition: ViewPosition,

    @SerializedName("quote")
    val quote: Quote,

    @SerializedName("sumario_list")
    var sumarioList: List<Sumario>

): MainTopic(), Serializable {

}