package com.tiagohs.entities

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.image.Image
import java.io.Serializable

class Recomendation(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("link")
    val link: String? = null
): Serializable