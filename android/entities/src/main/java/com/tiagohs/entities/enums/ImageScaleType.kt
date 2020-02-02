package com.tiagohs.entities.enums

import android.widget.ImageView
import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ImageScaleType(
    val imageScale: String,
    val imageViewEnum: ImageView.ScaleType
): Serializable {
    @SerializedName("matrix")
    MATRIX("matrix", ImageView.ScaleType.MATRIX),

    @SerializedName("fit_xy")
    FIT_XY("fit_xy", ImageView.ScaleType.FIT_XY),

    @SerializedName("fit_start")
    FIT_START("fit_start", ImageView.ScaleType.FIT_START),

    @SerializedName("fit_center")
    FIT_CENTER("fit_center", ImageView.ScaleType.FIT_CENTER),

    @SerializedName("fit_end")
    FIT_END("fit_end", ImageView.ScaleType.FIT_END),

    @SerializedName("center")
    CENTER("center", ImageView.ScaleType.CENTER),

    @SerializedName("center_crop")
    CENTER_CROP("center_crop", ImageView.ScaleType.CENTER_CROP),

    @SerializedName("center_inside")
    CENTER_INSIDE("center_inside", ImageView.ScaleType.CENTER_INSIDE);

    companion object {

        fun getImageViewScaleType(type: String?): ImageView.ScaleType {
            var imageScaleEnum = CENTER_CROP.imageViewEnum

            for (typeValue in values()) {
                if (typeValue.imageScale == type) {
                    imageScaleEnum = typeValue.imageViewEnum
                    break
                }
            }

            return imageScaleEnum
        }

    }
}