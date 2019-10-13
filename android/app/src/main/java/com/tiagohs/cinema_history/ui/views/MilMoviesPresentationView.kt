package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.tmdb.Movie
import com.tiagohs.cinema_history.ui.configs.IView

interface MilMoviesPresentationView: IView {

    fun setupArguments()
    fun bindMovieList(list: List<Movie>)
}