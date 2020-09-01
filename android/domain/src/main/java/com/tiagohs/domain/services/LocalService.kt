package com.tiagohs.domain.services

import com.tiagohs.domain.services.config.BaseService
import com.tiagohs.domain.services.config.RetrofitConfig
import com.tiagohs.domain.services.retrofit.LocalServiceRetrofit
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.Page
import com.tiagohs.entities.Sumario
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.timeline.TimelineResult
import com.tiagohs.entities.tmdb.person.PersonExtraInfo
import io.reactivex.Observable

class LocalService(retrofitConfig: RetrofitConfig): BaseService(retrofitConfig) {

    fun getHomeContent(): Observable<List<HomeContentItem>> = buildLocalService(LocalServiceRetrofit::class.java).getHomeContent()

    fun getSumarioByMainTopicID(mainTopicId: Int): Observable<List<Sumario>> = buildLocalService(LocalServiceRetrofit::class.java).getSumarioByMainTopicID(mainTopicId)

    fun getPage(mainTopicId: Int, sumarioId: Int): Observable<Page> = buildLocalService(LocalServiceRetrofit::class.java).getPage(mainTopicId, sumarioId)

    fun getReferences(): Observable<List<Reference>> = buildLocalService(LocalServiceRetrofit::class.java).getReferences()

    fun getMainTopics(): Observable<List<MainTopic>> = buildLocalService(LocalServiceRetrofit::class.java).getMainTopics()

    fun getTimeline(id: Int): Observable<TimelineResult> = buildLocalService(LocalServiceRetrofit::class.java).getTimeline(id)

    fun getTimelineItems(): Observable<List<Int>> = buildLocalService(LocalServiceRetrofit::class.java).getTimelineItems()

    fun getMilMoviesMainTopics(): Observable<List<MainTopic>> = buildLocalService(LocalServiceRetrofit::class.java).getMilMoviesMainTopics()

    fun getDirectorsMainTopics(): Observable<List<MainTopic>> = buildLocalService(LocalServiceRetrofit::class.java).getDirectorsMainTopics()

    fun getSpecialPersons(): Observable<List<PersonExtraInfo>> = buildLocalService(LocalServiceRetrofit::class.java).getSpecialPersons()

}