package com.tiagohs.cinema_history.models.person_info

import com.tiagohs.cinema_history.enums.PersonInfoType
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.models.tmdb.person.Person

open class PersonInfo(
    val type: PersonInfoType,
    val person: Person
)