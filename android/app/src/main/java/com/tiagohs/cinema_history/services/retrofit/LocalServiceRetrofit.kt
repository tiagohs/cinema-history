package com.tiagohs.cinema_history.services.retrofit

import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.Page
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface LocalServiceRetrofit {

    @GET("page{pageNumber}")
    fun getPage(@Path("pageNumber") pageNumber: Int): Observable<Page>

    @GET("maintopics")
    fun getMainTopics(): Observable<List<MainTopic>>
}