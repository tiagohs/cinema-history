package com.tiagohs.domain.presenter

import com.tiagohs.entities.tmdb.Result
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.movie.Video
import com.tiagohs.domain.presenter.configs.BasePresenter
import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.services.OMDBService
import com.tiagohs.domain.services.TMDBService
import com.tiagohs.domain.views.MovieDetailsView
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.entities.tmdb.movie.Collection
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.R
import com.tiagohs.helpers.utils.MovieUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function4
import io.reactivex.functions.Function5
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailsPresenterImpl @Inject constructor(
    val tmdbService: TMDBService,
    val omdbService: OMDBService,
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
            .concatMap { movie ->
                this.movie = movie

                val imdbObservable =  if (movie.imdbId != null) fetchOMDBResult(movie, movie.imdbId!!) else Observable.just(movie)
                val directorId = movie.credits?.crew?.filter { it.job == "Director" }?.firstOrNull()?.id
                val directorSource = if (directorId != null) fetchDirectorMovies(movie, directorId, languageToUse) else  Observable.just(movie)
                val collectionSource = if (movie.belongsToCollection?.id != null) fetchCollection(movie, movie.belongsToCollection?.id!!, languageToUse) else Observable.just(movie)

                return@concatMap Observable.zip<Movie, Movie, Movie, Movie, Movie, Movie>(
                    fetchVideos(movie, languageToUse).subscribeOn(Schedulers.io()),
                    fetchExtraInfo(movie).subscribeOn(Schedulers.io()),
                    imdbObservable.subscribeOn(Schedulers.io()),
                    directorSource.subscribeOn(Schedulers.io()),
                    collectionSource.subscribeOn(Schedulers.io()),
                    Function5 { _, _, _, _, _ -> return@Function5 movie }
                )
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

    private fun fetchVideos(movie: Movie, languageToUse: String): Observable<Movie> {
        val id = movie.id ?: return Observable.just(movie)

        val listOfObservables = ArrayList<Observable<Result<Video>>>()
        val translation = movie.translations?.translations?.find { it.iso_639_1 != "pt" && it.iso_639_1 != "en" && it.iso_639_1 == movie.originalLanguage }

        translation?.let {
            val originalLanguage = "${it.iso_639_1}-${it.iso_3166_1}"

            listOfObservables.add(tmdbService.getMovieVideos(id, originalLanguage))
        }

        listOfObservables.add(tmdbService.getMovieVideos(id, languageToUse))
        listOfObservables.add(tmdbService.getMovieVideos(id, "en-US"))

        return Observable.concat(listOfObservables)
                    .map { mapMovieWithVideos(it) }
                    .onErrorResumeNext { error: Throwable -> return@onErrorResumeNext Observable.just(movie) }
    }

    private fun mapMovieWithVideos(videos: Result<Video>): Movie {
        val arrayList = ArrayList(this.movie.videos?.videoList ?: emptyList())
        arrayList.addAll(videos.results ?: emptyList())

        this.movie.videos?.videoList = arrayList

        return this.movie
    }

    private fun fetchExtraInfo(movie: Movie): Observable<Movie> =
        localService.getSpecialMovies()
            .map {  movieExtraInfoListResult ->
                movieExtraInfoListResult.find { moviesExtras ->
                    val movieExtra = moviesExtras.movies?.find { moviesExtra -> moviesExtra.id == movie.id }

                    if (movieExtra != null) {
                        movie.extraInfo = movieExtra

                        val historyMainTopic = localService.getMainTopics().map { mainTopics -> mainTopics.find { mainTopic -> (mainTopic as? MainTopicItem)?.id == moviesExtras.historyMainTopicID } as? MainTopicItem }?.blockingFirst()
                        val isBlocked = historyMainTopic?.blocked ?: false
                        if (historyMainTopic != null && !isBlocked) {
                            movie.extraInfo?.historyMainTopic = historyMainTopic
                        }

                        movie.extraInfo?.milMoviesMainTopic = localService.getMilMoviesMainTopics().map { mainTopics -> mainTopics.find { mainTopic -> (mainTopic as? MilMoviesMainTopic)?.id == moviesExtras.milMoviesMainTopicID } as? MilMoviesMainTopic }?.blockingFirst()

                        return@find true
                    }

                    return@find false
                }

                movie
            }
            .onErrorResumeNext { _: Throwable -> Observable.just(movie) }

    private fun fetchCollection(movie: Movie, collectionId: Int, languageToUse: String) : Observable<Movie> =
        tmdbService.getCollection(collectionId, languageToUse)
            .map {
                movie.movieCollection = it

                movie
            }
            .onErrorResumeNext { _: Throwable -> Observable.just(movie) }

    private fun fetchOMDBResult(movie: Movie, imdbID: String) : Observable<Movie> =
        omdbService.getMovie(imdbID)
            .map {
                movie.omdbResult = it

                movie
            }
            .onErrorResumeNext { _: Throwable -> Observable.just(movie) }

    private fun fetchDirectorMovies(movie: Movie, directorId: Int, languageToUse: String) : Observable<Movie> =
        tmdbService.getPersonMovieCredits(directorId, languageToUse)
            .map {
                movie.directorMovies = MovieUtils.generatePersonMovieCredits(it)

                movie
            }
            .onErrorResumeNext { _: Throwable -> Observable.just(movie) }
}