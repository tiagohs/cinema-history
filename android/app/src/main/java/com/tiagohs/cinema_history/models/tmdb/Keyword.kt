package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName

class Keyword(
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String
) {
}