package com.tiagohs.cinema_history.presenter

import com.tiagohs.cinema_history.models.tmdb.Result
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.models.tmdb.movie.Video
import com.tiagohs.cinema_history.presenter.configs.BasePresenter
import com.tiagohs.cinema_history.services.TMDBService
import com.tiagohs.cinema_history.ui.views.MovieDetailsView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailsPresenterImpl @Inject constructor(
    val tmdbService: TMDBService
): BasePresenter<MovieDetailsView>(), MovieDetailsPresenter {

    lateinit var movie: Movie

    override fun onBindView(view: MovieDetailsView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchMovieDetails(movieId: Int) {
        val appendToResponse = listOf("credits", "images", "videos", "keywords", "releases", "similar_movies")

        view?.startLoading()
        add(tmdbService.getMovieDetails(movieId, appendToResponse)
            .concatMap { fetchVideos(it) }
            .map { mapMovieWithVideos(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.bindMovieDetails(it)
            }, {
                view?.onError(it, "Houve um erro inesperado, tente novamente.")
                view?.hideLoading()
            })
        )
    }

    private fun fetchVideos(movie: Movie): Observable<Result<Video>> {
        this.movie = movie

        val hasVideos = movie.videos?.videoList?.isEmpty() ?: false

        if (hasVideos) {
            return Observable.just(Result(results = movie.videos?.videoList))
        }

        val languages = movie.translations?.translations?.map { "${it.iso_639_1}-${it.iso_3166_1}" }?.joinToString(",") ?: "en-US,null"

        return tmdbService.getMovieVideos(movie.id!!, languages)
    }

    private fun mapMovieWithVideos(videos: Result<Video>): Movie {
        this.movie.videos?.videoList = videos.results

        return this.movie
    }
}