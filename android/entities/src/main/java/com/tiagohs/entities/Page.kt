package com.tiagohs.entities

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.contents.Content
import java.io.Serializable

data class Page(

    @SerializedName("number")
    val number: Int,

    @SerializedName("content_list")
    val contentList: List<Content>
): Serializable {

}