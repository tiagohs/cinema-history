package com.tiagohs.entities.movie_info

import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.enums.MovieInfoType

class MovieInfoPersonList(
    type: MovieInfoType,
    movie: Movie,
    val personList: List<PersonDTO>,
    val listTitle: String
): MovieInfo(type, movie)