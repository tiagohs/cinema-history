package com.tiagohs.domain.services.retrofit

import com.tiagohs.entities.Glossary
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.Page
import com.tiagohs.entities.Sumario
import com.tiagohs.entities.awards.NomineeResult
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentNominee
import com.tiagohs.entities.references.ReferenceResult
import com.tiagohs.entities.timeline.TimelineResult
import com.tiagohs.entities.tmdb.MovieExtraInfoResult
import com.tiagohs.entities.tmdb.person.PersonExtraInfo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface LocalServiceRetrofit {

    @GET("homecontent")
    fun getHomeContent(): Observable<List<HomeContentItem>>

    @GET("main_{mainTopicId}/page_{pageNumber}")
    fun getPage(@Path("mainTopicId") mainTopicId: Int, @Path("pageNumber") pageNumber: Int): Observable<Page>

    @GET("maintopics")
    fun getMainTopics(): Observable<List<MainTopic>>

    @GET("awards")
    fun getAwardsMainTopics(): Observable<List<MainTopic>>

    @GET("awards/nominees/{awardId}")
    fun fetchAwardsNominees(@Path("awardId") awardId: Int): Observable<List<NomineeResult>>

    @GET("awards/history/{awardId}")
    fun fetchAwardsHistory(@Path("awardId") awardId: Int): Observable<List<Content>>

    @GET("references")
    fun getReferences(): Observable<List<ReferenceResult>>

    @GET("glossary")
    fun getGlossary(): Observable<List<Glossary>>

    @GET("hmt_sumario_{mainTopicId}")
    fun getSumarioByMainTopicID(@Path("mainTopicId") mainTopicId: Int): Observable<List<Sumario>>

    @GET("milmoviesmaintopics")
    fun getMilMoviesMainTopics(): Observable<List<MainTopic>>

    @GET("directorsmaintopics")
    fun getDirectorsMainTopics(): Observable<List<MainTopic>>

    @GET("timeline_{id}")
    fun getTimeline(@Path("id") id: Int): Observable<TimelineResult>

    @GET("timelines")
    fun getTimelineItems(): Observable<List<Int>>

    @GET("special_persons")
    fun getSpecialPersons(): Observable<List<PersonExtraInfo>>

    @GET("special_movies")
    fun getSpecialMovies(): Observable<List<MovieExtraInfoResult>>
}