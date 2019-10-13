package com.tiagohs.cinema_history.services.retrofit

import com.tiagohs.cinema_history.models.tmdb.Movie
import com.tiagohs.cinema_history.models.tmdb.TMDBList
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBServiceRetrofit {

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String
    ): Observable<Movie>

    @GET("list/{list_id}")
    fun getList(
        @Path("list_id") listId: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ): Observable<TMDBList>
}