package com.tiagohs.domain.presenter

import com.tiagohs.entities.tmdb.Result
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.movie.Video
import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.services.TMDBService
import com.tiagohs.domain.views.MovieDetailsView
import com.tiagohs.entities.tmdb.movie.Collection
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.R
import com.tiagohs.helpers.utils.MovieUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailsPresenterImpl @Inject constructor(
    val tmdbService: TMDBService,
    val localService: LocalService
): BasePresenter<MovieDetailsView>(), MovieDetailsPresenter {

    lateinit var movie: Movie

    override fun onBindView(view: MovieDetailsView) {
        super.onBindView(view)

        this.view?.setupArguments()
    }

    override fun fetchMovieDetails(movieId: Int, languageToUse: String) {
        val appendToResponse = listOf("credits", "images", "videos", "keywords", "releases", "similar_movies", "external_ids", "translations")

        view?.startLoading()
        add(tmdbService.getMovieDetails(movieId, languageToUse, appendToResponse)
            .doOnNext { it.setupImages() }
            .concatMap { fetchVideos(it, languageToUse) }
            .map { this.movie }
            .concatMap { fetchExtraInfo(it) }
            .concatMap { movie ->
                val directorId = movie.credits?.crew?.filter { it.job == "Director" }?.firstOrNull()?.id ?: return@concatMap Observable.just(movie)

                fetchDirectorMovies(movie, directorId, languageToUse)
            }
            .concatMap { movie ->
                val collectionId = movie.belongsToCollection?.id ?: return@concatMap Observable.just(movie)

                fetchCollection(movie, collectionId, languageToUse)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                this.movie = it
            }, {
                view?.showError {
                    view?.startLoading()
                    view?.hideError()

                    fetchMovieDetails(movieId, languageToUse)
                }
                view?.onError(it, R.string.error_unknown)
                view?.hideLoading()
            }, {
                view?.hideLoading()
                view?.bindMovieDetails(this.movie)
            })
        )
    }

    private fun fetchVideos(movie: Movie, languageToUse: String): Observable<Result<Video>> {
        this.movie = movie
        val id = movie.id ?: return Observable.just(Result(results = movie.videos?.videoList))

        val listOfObservables = ArrayList<Observable<Result<Video>>>()
        val translation = movie.translations?.translations?.find { it.iso_639_1 != "pt" && it.iso_639_1 != "en" && it.iso_639_1 == movie.originalLanguage }

        translation?.let {
            val originalLanguage = "${it.iso_639_1}-${it.iso_3166_1}"

            listOfObservables.add(tmdbService.getMovieVideos(id, originalLanguage))
        }

        listOfObservables.add(tmdbService.getMovieVideos(id, languageToUse))
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

    private fun fetchExtraInfo(movie: Movie): Observable<Movie> =
        localService.getSpecialMovies()
            .map {  movieExtraInfoList ->
                movieExtraInfoList.find {
                        movieExtra -> movieExtra.id == movie.id
                }?.let {
                    movie.extraInfo = it
                }

                movie
            }

    private fun fetchCollection(movie: Movie, collectionId: Int, languageToUse: String) : Observable<Movie> =
        tmdbService.getCollection(collectionId, languageToUse)
            .map {
                movie.movieCollection = it

                movie
            }

    private fun fetchDirectorMovies(movie: Movie, directorId: Int, languageToUse: String) : Observable<Movie> =
        tmdbService.getPersonMovieCredits(directorId, languageToUse)
            .map {
                movie.directorMovies = MovieUtils.generatePersonMovieCredits(it)

                movie
            }
}