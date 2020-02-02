package com.tiagohs.domain.views

import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.domain.views.configs.IView

interface MovieDetailsView: IView {
    fun setupArguments()
    fun bindMovieDetails(movie: Movie)

    fun startLoading()
    fun hideLoading()
}