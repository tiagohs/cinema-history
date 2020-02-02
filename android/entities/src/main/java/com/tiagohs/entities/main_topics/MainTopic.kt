package com.tiagohs.entities.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.MainTopicItemLayoutType
import com.tiagohs.entities.enums.MainTopicsType
import java.io.Serializable

open class MainTopic(): Serializable {

    @SerializedName("layout_type")
    val layoutType: MainTopicItemLayoutType = MainTopicItemLayoutType.CARD

    @SerializedName("main_topic_type")
    val mainTopicType: MainTopicsType? = MainTopicsType.HISTORY_CINEMA

}