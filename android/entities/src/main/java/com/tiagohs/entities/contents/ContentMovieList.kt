package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.tmdb.movie.Movie
import java.io.Serializable

data class ContentMovieList(
    @SerializedName("movies")
    var movies: List<Movie>? = null
): Content(), Serializable