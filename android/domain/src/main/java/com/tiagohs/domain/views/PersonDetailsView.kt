package com.tiagohs.domain.views

import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.domain.views.configs.IView

interface PersonDetailsView: IView {

    fun setupArguments()
    fun bindPersonDetails(person: Person)

    fun startLoading()
    fun hideLoading()
}