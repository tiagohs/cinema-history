package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.NetworkType
import java.io.Serializable

data class Network(
    @SerializedName("type") val type : NetworkType = NetworkType.UNKNOWN,
    @SerializedName("name") val name : String? = null,
    @SerializedName("link") val link : String? = null
): Serializable