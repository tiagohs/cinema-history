package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.tmdb.person.Person
import com.tiagohs.cinema_history.ui.configs.IView

interface PersonDetailsView: IView {

    fun setupArguments()
    fun bindPersonDetails(person: Person)
}