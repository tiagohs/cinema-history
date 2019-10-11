package com.tiagohs.cinema_history.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType

open class MainTopic() : Parcelable {

    @SerializedName("layout_type")
    val layoutType: MainTopicItemLayoutType = MainTopicItemLayoutType.CARD

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainTopic> {
        override fun createFromParcel(parcel: Parcel): MainTopic {
            return MainTopic(parcel)
        }

        override fun newArray(size: Int): Array<MainTopic?> {
            return arrayOfNulls(size)
        }
    }
}