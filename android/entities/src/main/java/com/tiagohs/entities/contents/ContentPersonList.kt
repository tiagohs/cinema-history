package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.person.Person
import java.io.Serializable

data class ContentPersonList(
    @SerializedName("persons")
    var persons: List<Person>? = null
): Content(), Serializable