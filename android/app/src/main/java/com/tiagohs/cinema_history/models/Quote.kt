package com.tiagohs.cinema_history.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Quote(

    @SerializedName("id")
    val id: Int,

    @SerializedName("quote")
    val quote: String,

    @SerializedName("author")
    val author: String
): MainTopic(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(id)
        parcel.writeString(quote)
        parcel.writeString(author)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quote> {
        override fun createFromParcel(parcel: Parcel): Quote {
            return Quote(parcel)
        }

        override fun newArray(size: Int): Array<Quote?> {
            return arrayOfNulls(size)
        }
    }
}