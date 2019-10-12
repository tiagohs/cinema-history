package com.tiagohs.cinema_history.models.image

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.enums.AnimationType
import java.io.Serializable

data class Animation(

    @SerializedName("type")
    val type: AnimationType,

    @SerializedName("duration")
    val duration: Int
): Serializable {

}