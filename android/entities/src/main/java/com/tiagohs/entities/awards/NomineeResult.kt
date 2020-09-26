package com.tiagohs.entities.awards

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.contents.Content
import java.io.Serializable

class NomineeResult(
    @SerializedName("year")
    val year: String? = null,

    @SerializedName("content")
    val content: List<Content>? = null
) : Serializable