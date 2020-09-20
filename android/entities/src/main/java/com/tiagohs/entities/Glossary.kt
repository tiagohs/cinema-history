package com.tiagohs.entities

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.contents.Content
import java.io.Serializable

class Glossary(
    @SerializedName("name")
    val name: String,

    @SerializedName("content_list")
    val contentList: List<Content>
): Serializable