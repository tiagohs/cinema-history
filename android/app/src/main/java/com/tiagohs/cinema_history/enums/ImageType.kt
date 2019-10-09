package com.tiagohs.cinema_history.enums

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

enum class ImageType(
    val type: String
): Parcelable {

    @SerializedName("local")
    LOCAL("local"),

    @SerializedName("online")
    ONLINE("online");

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageType> {
        override fun createFromParcel(parcel: Parcel): ImageType {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<ImageType?> {
            return arrayOfNulls(size)
        }
    }
}