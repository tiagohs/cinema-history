package com.tiagohs.cinema_history.models

import com.google.gson.annotations.SerializedName

class Sumario(
    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("pages")
    val pages: List<Page>
) {
}