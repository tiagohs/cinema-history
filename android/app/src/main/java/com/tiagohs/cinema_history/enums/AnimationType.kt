package com.tiagohs.cinema_history.enums

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

enum class AnimationType(
    val type: String
): Parcelable {

    @SerializedName("shake_vertical")
    SHAKE_VERTICAL("shake_vertical");

    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnimationType> {
        override fun createFromParcel(parcel: Parcel): AnimationType {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<AnimationType?> {
            return arrayOfNulls(size)
        }
    }
}