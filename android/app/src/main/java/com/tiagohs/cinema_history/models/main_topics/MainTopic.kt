package com.tiagohs.cinema_history.models.main_topics

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType
import com.tiagohs.cinema_history.enums.MainTopicsType
import java.io.Serializable

open class MainTopic(): Serializable {

    @SerializedName("layout_type")
    val layoutType: MainTopicItemLayoutType = MainTopicItemLayoutType.CARD

    @SerializedName("main_topic_type")
    val mainTopicType: MainTopicsType? = MainTopicsType.HISTORY_CINEMA

}