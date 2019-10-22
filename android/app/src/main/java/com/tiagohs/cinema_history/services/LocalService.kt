package com.tiagohs.cinema_history.services

import com.tiagohs.cinema_history.models.main_topics.MainTopic
import com.tiagohs.cinema_history.models.Page
import com.tiagohs.cinema_history.models.Sumario
import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.services.config.BaseService
import com.tiagohs.cinema_history.services.retrofit.LocalServiceRetrofit
import io.reactivex.Observable

class LocalService: BaseService() {

    fun getSumarioByMainTopicID(mainTopicId: Int): Observable<List<Sumario>> = buildLocalService(LocalServiceRetrofit::class.java).getSumarioByMainTopicID(mainTopicId)
    fun getPage(mainTopicId: Int, sumarioId: Int): Observable<Page> = buildLocalService(LocalServiceRetrofit::class.java).getPage(mainTopicId, sumarioId)
    fun getMainTopics(): Observable<List<MainTopic>> = buildLocalService(LocalServiceRetrofit::class.java).getMainTopics()
    fun getTimeline(): Observable<List<Timeline>> = buildLocalService(LocalServiceRetrofit::class.java).getTimeline()
    fun getMilMoviesMainTopics(): Observable<List<MainTopic>> = buildLocalService(LocalServiceRetrofit::class.java).getMilMoviesMainTopics()
}