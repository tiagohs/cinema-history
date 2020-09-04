package com.tiagohs.domain.services.retrofit

import com.tiagohs.entities.omdb.OMDBResult
import com.tiagohs.entities.tmdb.movie.Movie
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface OMDBServiceRetrofit {

    @GET("/")
    fun getMovie(
        @Query("i") imdbID: String
    ): Observable<OMDBResult>
}