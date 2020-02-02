package com.tiagohs.entities.movie_info

import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.enums.MovieInfoType

open class MovieInfo(
    val type: MovieInfoType,
    val movie: Movie
)