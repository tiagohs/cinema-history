package com.tiagohs.cinema_history.services

import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.models.tmdb.TMDBList
import com.tiagohs.cinema_history.models.tmdb.person.Person
import com.tiagohs.cinema_history.services.config.BaseService
import com.tiagohs.cinema_history.services.retrofit.TMDBServiceRetrofit
import io.reactivex.Observable

class TMDBService: BaseService() {

    fun getMovieDetails(movieId: Int, appendToResponse: List<String>): Observable<Movie> {
        return buildTMDB3Service(TMDBServiceRetrofit::class.java).getMovieDetails(movieId, "en-EN", appendToResponse.joinToString(","))
    }

    fun getPersonDetails(personId: Int, appendToResponse: List<String>): Observable<Person> {
        return buildTMDB3Service(TMDBServiceRetrofit::class.java).getPersonDetails(personId, "en-EN", appendToResponse.joinToString(","))
    }

    fun getList(listId: String, page: Int): Observable<TMDBList> {
        return buildTMDB4Service(TMDBServiceRetrofit::class.java).getList(listId, page, "null")
    }
}