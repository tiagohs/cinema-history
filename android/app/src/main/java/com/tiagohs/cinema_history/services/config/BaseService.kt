package com.tiagohs.cinema_history.services.config

import com.tiagohs.cinema_history.helpers.utils.RetrofitUtil

abstract class BaseService {

    fun <T> buildTMDBService(mClass: Class<T>): T {
        return RetrofitUtil.tmdbBuild().create(mClass)
    }

    fun <T> buildLocalService(mClass: Class<T>): T {
        return RetrofitUtil.localBuild().create(mClass)
    }
}