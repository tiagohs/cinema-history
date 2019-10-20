package com.tiagohs.cinema_history.models.movie_info

import com.tiagohs.cinema_history.enums.MovieInfoType
import com.tiagohs.cinema_history.models.PersonDTO
import com.tiagohs.cinema_history.models.tmdb.movie.Movie

class MovieInfoPersonList(
    type: MovieInfoType,
    movie: Movie,
    val personList: List<PersonDTO>,
    val listTitle: String
): MovieInfo(type, movie)