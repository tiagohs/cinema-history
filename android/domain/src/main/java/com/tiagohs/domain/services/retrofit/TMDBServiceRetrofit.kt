package com.tiagohs.domain.services.retrofit

import com.tiagohs.entities.tmdb.Result
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.TMDBList
import com.tiagohs.entities.tmdb.movie.Video
import com.tiagohs.entities.tmdb.person.Person
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBServiceRetrofit {

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String
    ): Observable<Movie>

    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String
    ): Observable<Result<Video>>

    @GET("person/{personId}")
    fun getPersonDetails(
        @Path("personId") personId: Int,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String
    ): Observable<Person>

    @GET("list/{list_id}")
    fun getList(
        @Path("list_id") listId: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ): Observable<TMDBList>
}