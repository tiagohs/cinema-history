package com.tiagohs.cinema_history.models.movie_info

import com.tiagohs.cinema_history.enums.MovieInfoType
import com.tiagohs.cinema_history.models.tmdb.movie.Movie

open class MovieInfo(
    val type: MovieInfoType,
    val movie: Movie
)