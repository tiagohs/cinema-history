package com.tiagohs.entities.awards

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.SocialType
import java.io.Serializable

data class Social(
    @SerializedName("type")
    val type: SocialType,

    @SerializedName("link")
    val link: String? = null,
): Serializable