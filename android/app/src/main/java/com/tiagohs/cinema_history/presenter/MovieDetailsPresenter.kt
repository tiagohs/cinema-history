package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.MovieDetailsView

interface MovieDetailsPresenter: IPresenter<MovieDetailsView> {
    fun fetchMovieDetails(movieId: Int)
}