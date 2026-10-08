package com.tiagohs.domain.services

import com.tiagohs.helpers.utils.ContentLanguage
import com.tiagohs.entities.tmdb.Result
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.TMDBList
import com.tiagohs.entities.tmdb.movie.Video
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.domain.services.config.BaseService
import com.tiagohs.domain.services.config.RetrofitConfig
import com.tiagohs.domain.services.retrofit.TMDBServiceRetrofit
import com.tiagohs.entities.tmdb.movie.Collection
import com.tiagohs.entities.tmdb.movie.MovieImages
import com.tiagohs.entities.tmdb.person.PersonMovieCredits
import io.reactivex.Observable

class TMDBService(retrofitConfig: RetrofitConfig): BaseService(retrofitConfig) {

    fun getMovieDetails(movieId: Int, languageToUse: String, appendToResponse: List<String>): Observable<Movie> {
        return buildTMDB3Service(TMDBServiceRetrofit::class.java).getMovieDetails(movieId, languageToUse, appendToResponse.joinToString(","))
    }

    /** [language]: idioma da resposta; [includeVideoLanguages]: idiomas aceitos nos vídeos (ex.: "pt,en,null"). */
    fun getMovieVideos(movieId: Int, language: String, includeVideoLanguages: String): Observable<Result<Video>> {
        return buildTMDB3Service(TMDBServiceRetrofit::class.java).getMovieVideos(movieId, language, includeVideoLanguages)
    }

    fun getMovieImages(movieId: Int, languages: String, includeImageLanguage: String): Observable<MovieImages> {
        return buildTMDB3Service(TMDBServiceRetrofit::class.java).getMovieImages(movieId, languages, includeImageLanguage)
    }

    fun getPersonDetails(personId: Int, languageToUse: String, appendToResponse: List<String>): Observable<Person> {
        return buildTMDB3Service(TMDBServiceRetrofit::class.java).getPersonDetails(personId, languageToUse, appendToResponse.joinToString(","))
    }

    fun getPersonMovieCredits(personId: Int, language: String): Observable<PersonMovieCredits> = buildTMDB3Service(TMDBServiceRetrofit::class.java).getPersonMovieCredits(personId, language)

    fun getList(listId: String, page: Int): Observable<TMDBList> {
        return buildTMDB4Service(TMDBServiceRetrofit::class.java).getList(listId, page, ContentLanguage.tmdbTag())
    }

    fun getCollection(collectionId: Int, language: String): Observable<Collection> = buildTMDB3Service(TMDBServiceRetrofit::class.java).getCollection(collectionId, language)
}