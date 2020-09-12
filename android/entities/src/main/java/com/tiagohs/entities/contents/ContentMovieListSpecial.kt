package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.tmdb.movie.Movie
import java.io.Serializable

class ContentMovieListSpecial(
    @SerializedName("movies")
    var movies: List<Movie>? = null,

    @SerializedName("information")
    var information: ContentInformation
): Content(), Serializable