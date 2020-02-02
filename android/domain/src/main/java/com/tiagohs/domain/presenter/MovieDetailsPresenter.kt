package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.MovieDetailsView

interface MovieDetailsPresenter: IPresenter<MovieDetailsView> {
    fun fetchMovieDetails(movieId: Int)
}