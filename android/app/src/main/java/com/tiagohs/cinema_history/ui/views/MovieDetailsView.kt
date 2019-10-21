package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.ui.configs.IView

interface MovieDetailsView: IView {
    fun setupArguments()
    fun bindMovieDetails(movie: Movie)

    fun startLoading()
    fun hideLoading()
}