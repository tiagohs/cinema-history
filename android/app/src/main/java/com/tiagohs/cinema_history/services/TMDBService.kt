package com.tiagohs.cinema_history.services

import com.tiagohs.cinema_history.models.tmdb.Movie
import com.tiagohs.cinema_history.models.tmdb.TMDBList
import com.tiagohs.cinema_history.services.config.BaseService
import com.tiagohs.cinema_history.services.retrofit.TMDBServiceRetrofit
import io.reactivex.Observable

class TMDBService: BaseService() {

    fun getMovieDetails(movieId: String, appendToResponse: List<String>): Observable<Movie> {
        return buildTMDBService(TMDBServiceRetrofit::class.java).getMovieDetails(movieId, "pt-BR,en-EN,null", appendToResponse.joinToString(","))
    }

    fun getList(listId: String, page: Int): Observable<TMDBList> {
        return buildTMDBService(TMDBServiceRetrofit::class.java).getList(listId, page, "null")
    }
}