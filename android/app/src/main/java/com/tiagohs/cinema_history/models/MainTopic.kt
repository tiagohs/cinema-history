package com.tiagohs.cinema_history.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.image.Image

data class MainTopic(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("subtitle")
    val subtitle: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("image")
    val image: Image,

    @SerializedName("title_color")
    val titleColor: String,

    @SerializedName("title_background_color")
    val titleBackgroundColor: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Image::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeString(description)
        parcel.writeParcelable(image, flags)
        parcel.writeString(titleColor)
        parcel.writeString(titleBackgroundColor)
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