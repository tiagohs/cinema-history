package com.tiagohs.domain.presenter

import com.tiagohs.entities.tmdb.Result
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.movie.Video
import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.TMDBService
import com.tiagohs.domain.views.MovieDetailsView
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
        val appendToResponse = listOf("credits", "images", "videos", "keywords", "releases", "similar_movies", "external_ids", "translations")

        view?.startLoading()
        add(tmdbService.getMovieDetails(movieId, appendToResponse)
            .concatMap { fetchVideos(it) }
            .map { this.movie }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                this.movie = it
            }, {
                view?.onError(it, "Houve um erro inesperado, tente novamente.")
                view?.hideLoading()
            }, {
                view?.hideLoading()
                view?.bindMovieDetails(this.movie)
            })
        )
    }

    private fun fetchVideos(movie: Movie): Observable<Result<Video>> {
        this.movie = movie
        val id = movie.id ?: return Observable.just(Result(results = movie.videos?.videoList))

        val listOfObservables = ArrayList<Observable<Result<Video>>>()
        val translation = movie.translations?.translations?.find { it.iso_639_1 != "pt" && it.iso_639_1 != "en" && it.iso_639_1 == movie.originalLanguage }

        translation?.let {
            val originalLanguage = "${it.iso_639_1}-${it.iso_3166_1}"

            listOfObservables.add(tmdbService.getMovieVideos(id, originalLanguage))
        }

        listOfObservables.add(tmdbService.getMovieVideos(id, "pt-BR"))
        listOfObservables.add(tmdbService.getMovieVideos(id, "en-US"))

        return Observable.concat(listOfObservables)
                    .doOnNext { mapMovieWithVideos(it) }
    }

    private fun mapMovieWithVideos(videos: Result<Video>): Movie {
        val arrayList = ArrayList(this.movie.videos?.videoList ?: emptyList())
        arrayList.addAll(videos.results ?: emptyList())

        this.movie.videos?.videoList = arrayList

        return this.movie
    }
}