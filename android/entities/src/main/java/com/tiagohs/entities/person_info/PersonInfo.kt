package com.tiagohs.entities.person_info

import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.entities.enums.PersonInfoType

open class PersonInfo(
    val type: PersonInfoType,
    val person: Person
)