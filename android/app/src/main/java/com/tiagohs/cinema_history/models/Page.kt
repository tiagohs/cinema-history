package com.tiagohs.cinema_history.models

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.contents.Content
import java.io.Serializable

data class Page(

    @SerializedName("number")
    val number: Int,

    @SerializedName("content_list")
    val contentList: List<Content>
): Serializable {

}