package com.tiagohs.domain.services

import com.tiagohs.domain.services.config.BaseService
import com.tiagohs.domain.services.config.RetrofitConfig
import com.tiagohs.domain.services.retrofit.OMDBServiceRetrofit
import com.tiagohs.entities.omdb.OMDBResult
import io.reactivex.Observable

class OMDBService(retrofitConfig: RetrofitConfig) : BaseService(retrofitConfig) {

    fun getMovie(imdbID: String): Observable<OMDBResult> =
        buildOMDBService(OMDBServiceRetrofit::class.java).getMovie(imdbID)
}