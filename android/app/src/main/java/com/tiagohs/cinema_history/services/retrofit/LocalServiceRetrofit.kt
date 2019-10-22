package com.tiagohs.cinema_history.services.retrofit

import com.tiagohs.cinema_history.models.main_topics.MainTopic
import com.tiagohs.cinema_history.models.Page
import com.tiagohs.cinema_history.models.Sumario
import com.tiagohs.cinema_history.models.timeline.Timeline
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface LocalServiceRetrofit {

    @GET("main_{mainTopicId}/page_{pageNumber}")
    fun getPage(@Path("mainTopicId") mainTopicId: Int, @Path("pageNumber") pageNumber: Int): Observable<Page>

    @GET("maintopics")
    fun getMainTopics(): Observable<List<MainTopic>>

    @GET("hmt_sumario_{mainTopicId}")
    fun getSumarioByMainTopicID(@Path("mainTopicId") mainTopicId: Int): Observable<List<Sumario>>

    @GET("milmoviesmaintopics")
    fun getMilMoviesMainTopics(): Observable<List<MainTopic>>

    @GET("timeline")
    fun getTimeline(): Observable<List<Timeline>>
}