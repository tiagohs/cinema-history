package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Keyword(
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String
): Serializable {
}