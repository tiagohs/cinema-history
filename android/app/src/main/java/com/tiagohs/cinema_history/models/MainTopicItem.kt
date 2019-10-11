package com.tiagohs.cinema_history.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType
import com.tiagohs.cinema_history.models.image.Image

data class MainTopicItem(
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
): MainTopic(), Parcelable {
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

    companion object CREATOR : Parcelable.Creator<MainTopicItem> {
        override fun createFromParcel(parcel: Parcel): MainTopicItem {
            return MainTopicItem(parcel)
        }

        override fun newArray(size: Int): Array<MainTopicItem?> {
            return arrayOfNulls(size)
        }
    }

}