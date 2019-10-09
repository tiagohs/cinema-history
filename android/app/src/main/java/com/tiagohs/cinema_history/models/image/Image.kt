package com.tiagohs.cinema_history.models.image

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.ImageType

class Image(

    @SerializedName("image_type")
    val imageType: ImageType = ImageType.ONLINE,

    @SerializedName("url")
    val url: String,

    @SerializedName("animation")
    val animation: Animation? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ImageType::class.java.classLoader),
        parcel.readString(),
        parcel.readParcelable(Animation::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(imageType, flags)
        parcel.writeString(url)
        parcel.writeParcelable(animation, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }

}
