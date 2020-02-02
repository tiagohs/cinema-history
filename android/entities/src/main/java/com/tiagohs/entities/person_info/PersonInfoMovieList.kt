package com.tiagohs.entities.person_info

import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.enums.PersonInfoType

class PersonInfoMovieList(
    type: PersonInfoType,
    person: Person,
    val movieList: List<MovieFilmographyDTO>,
    val listTitle: String
): PersonInfo(type, person)