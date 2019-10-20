package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.TMDBService
import com.tiagohs.cinema_history.ui.views.MovieDetailsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailsPresenterImpl @Inject constructor(
    val tmdbService: TMDBService
): BasePresenter<MovieDetailsView>(), MovieDetailsPresenter {

    override fun onBindView(view: MovieDetailsView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchMovieDetails(movieId: Int) {
        val appendToResponse = listOf("credits", "images", "videos", "keywords", "releases", "similar_movies")

        add(tmdbService.getMovieDetails(movieId, appendToResponse)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.bindMovieDetails(it)
            }, {

            })
        )
    }
}