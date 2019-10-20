package com.tiagohs.cinema_history.services.config

import com.tiagohs.cinema_history.helpers.utils.RetrofitUtil

abstract class BaseService {

    fun <T> buildTMDB3Service(mClass: Class<T>): T {
        return RetrofitUtil.tmdb3Build().create(mClass)
    }

    fun <T> buildTMDB4Service(mClass: Class<T>): T {
        return RetrofitUtil.tmdb4Build().create(mClass)
    }

    fun <T> buildLocalService(mClass: Class<T>): T {
        return RetrofitUtil.localBuild().create(mClass)
    }
}