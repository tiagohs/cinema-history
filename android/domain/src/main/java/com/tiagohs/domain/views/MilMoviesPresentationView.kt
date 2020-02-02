package com.tiagohs.domain.views

import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.domain.views.configs.IView

interface MilMoviesPresentationView: IView {

    fun setupArguments()
    fun bindMovieList(list: List<Movie>)
    fun bindMoreMovies(movies: List<Movie>)

    fun startLoading()
    fun hideLoading()
}