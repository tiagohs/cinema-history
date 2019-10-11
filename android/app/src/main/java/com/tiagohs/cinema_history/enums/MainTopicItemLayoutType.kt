package com.tiagohs.cinema_history.enums

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

enum class MainTopicItemLayoutType(
    val type: String
) : Parcelable {

    @SerializedName("card")
    CARD("card"),

    @SerializedName("full")
    FULL("full"),

    @SerializedName("quote")
    QUOTE("quote");


    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainTopicItemLayoutType> {
        override fun createFromParcel(parcel: Parcel): MainTopicItemLayoutType {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<MainTopicItemLayoutType?> {
            return arrayOfNulls(size)
        }

        fun getContentType(type: String): MainTopicItemLayoutType {
            var typeEnum = CARD

            for (typeValue in values()) {
                if (typeValue.type == type) {
                    typeEnum = typeValue
                    break
                }
            }

            return typeEnum
        }

    }
}