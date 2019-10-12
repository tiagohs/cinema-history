package com.tiagohs.cinema_history.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType
import java.io.Serializable

open class MainTopic(): Serializable {

    @SerializedName("layout_type")
    val layoutType: MainTopicItemLayoutType = MainTopicItemLayoutType.CARD

}