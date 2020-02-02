package com.tiagohs.entities.image

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.AnimationType
import java.io.Serializable

data class Animation(

    @SerializedName("type")
    val type: AnimationType,

    @SerializedName("duration")
    val duration: Int
): Serializable {

}