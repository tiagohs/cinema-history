package com.tiagohs.cinema_history.services.retrofit

import com.tiagohs.cinema_history.models.tmdb.Movie
import com.tiagohs.cinema_history.models.tmdb.TMDBList
import io.reactivex.Observable

interface TMDBServiceRetrofit {

    fun getMovieDetails(movieId: String, language: String): Observable<Movie>
    fun getList(listId: String, page: Int, language: String): Observable<TMDBList>
}