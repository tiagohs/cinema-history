package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.tmdb.Image
import java.io.Serializable


data class MovieImages (
    @SerializedName("backdrops") val backdrops : List<Image>? = null,
    @SerializedName("posters") val posters : List<Image>? = null
): Serializable