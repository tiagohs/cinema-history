package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.EssayChannel
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.person.Person
import java.io.Serializable

open class ContentEssay(
    @SerializedName("title")
    var title: String,

    @SerializedName("description")
    var description: String,

    @SerializedName("video_id")
    val videoId: String,

    @SerializedName("channel")
    var channel: EssayChannel? = null,

    @SerializedName("person")
    var person: Person? = null,

    @SerializedName("movie")
    var movie: Movie? = null
): Content(), Serializable