package com.tiagohs.cinema_history.ui.views

import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.ui.configs.IView

interface MilMoviesPresentationView: IView {

    fun setupArguments()
    fun bindMovieList(list: List<Movie>)
    fun bindMoreMovies(movies: List<Movie>)

    fun startLoading()
    fun hideLoading()
}