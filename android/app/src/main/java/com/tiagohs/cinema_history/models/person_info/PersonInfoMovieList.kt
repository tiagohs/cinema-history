package com.tiagohs.cinema_history.models.movie_info

import com.tiagohs.cinema_history.enums.PersonInfoType
import com.tiagohs.cinema_history.models.MovieFilmographyDTO
import com.tiagohs.cinema_history.models.PersonDTO
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.models.tmdb.person.Person

class PersonInfoMovieList(
    type: PersonInfoType,
    person: Person,
    val movieList: List<MovieFilmographyDTO>,
    val listTitle: String
): PersonInfo(type, person)