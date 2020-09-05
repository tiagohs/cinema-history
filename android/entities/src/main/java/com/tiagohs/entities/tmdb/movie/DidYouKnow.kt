package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DidYouKnow(
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null
) : Serializable