package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.IPresenter
import com.tiagohs.cinema_history.ui.views.MilMoviesPresentationView

interface MilMoviesPresentationPresenter: IPresenter<MilMoviesPresentationView> {

    fun fetchMoviesByListId(listId: String)
    fun fetchMoreMovies(listId: String, page: Int)

    fun hasMorePages(): Boolean
}