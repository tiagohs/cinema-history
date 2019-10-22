package com.tiagohs.cinema_history.models.movie_info

import com.tiagohs.cinema_history.enums.PersonInfoType
import com.tiagohs.cinema_history.models.dto.MovieFilmographyDTO
import com.tiagohs.cinema_history.models.tmdb.person.Person

class PersonInfoMovieList(
    type: PersonInfoType,
    person: Person,
    val movieList: List<MovieFilmographyDTO>,
    val listTitle: String
): PersonInfo(type, person)