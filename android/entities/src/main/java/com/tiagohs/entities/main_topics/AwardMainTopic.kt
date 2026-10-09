package com.tiagohs.entities.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.awards.AwardYearSummary
import com.tiagohs.entities.contents.ContentNominee
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

    @SerializedName("logo")
    val logo: Image,

    @SerializedName("nominees_id")
    val nominees_id: Int? = null,

    @SerializedName("social_list")
    val socialList: List<Social>? = null,
): MainTopic() , Serializable {

    var history: List<Content>? = null
    /** Índice de anos (mais recente primeiro). O conteúdo de cada ano é carregado sob demanda. */
    var yearIndex: List<AwardYearSummary>? = null
}