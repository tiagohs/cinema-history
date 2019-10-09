package com.tiagohs.cinema_history.services

import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.Page
import com.tiagohs.cinema_history.services.config.BaseService
import com.tiagohs.cinema_history.services.retrofit.LocalServiceRetrofit
import io.reactivex.Observable

class LocalService: BaseService() {

    fun getPage(pageNumber: Int): Observable<Page> = buildLocalService(LocalServiceRetrofit::class.java).getPage(pageNumber)

    fun getMainTopics(): Observable<List<MainTopic>> = buildLocalService(LocalServiceRetrofit::class.java).getMainTopics()
}