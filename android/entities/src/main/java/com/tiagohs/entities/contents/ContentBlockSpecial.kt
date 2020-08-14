package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.image.Image
import java.io.Serializable

class ContentBlockSpecial(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("image")
    val image: Image,

    @SerializedName("click")
    val click: Click? = null
): Content(), Serializable