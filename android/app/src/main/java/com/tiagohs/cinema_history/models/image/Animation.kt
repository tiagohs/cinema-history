package com.tiagohs.cinema_history.models.image

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.AnimationType

data class Animation(

    @SerializedName("type")
    val type: AnimationType,

    @SerializedName("duration")
    val duration: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(AnimationType::class.java.classLoader),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(type, flags)
        parcel.writeInt(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Animation> {
        override fun createFromParcel(parcel: Parcel): Animation {
            return Animation(parcel)
        }

        override fun newArray(size: Int): Array<Animation?> {
            return arrayOfNulls(size)
        }
    }

}