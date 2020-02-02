package com.tiagohs.domain.presenter

import com.tiagohs.domain.presenter.configs.IPresenter
import com.tiagohs.domain.views.MilMoviesPresentationView

interface MilMoviesPresentationPresenter: IPresenter<MilMoviesPresentationView> {

    fun fetchMoviesByListId(listId: String)
    fun fetchMoreMovies(listId: String, page: Int)

    fun hasMorePages(): Boolean
}