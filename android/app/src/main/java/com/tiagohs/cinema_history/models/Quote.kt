package com.tiagohs.cinema_history.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Quote(

    @SerializedName("id")
    val id: Int,

    @SerializedName("quote")
    val quote: String,

    @SerializedName("author")
    val author: String
): MainTopic(), Serializable {

}