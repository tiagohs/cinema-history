package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.Recomendation
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.image.Image
import java.io.Serializable

class ContentRecomendation(
    @SerializedName("list")
    val list: List<Recomendation>? = null
): Content(), Serializable