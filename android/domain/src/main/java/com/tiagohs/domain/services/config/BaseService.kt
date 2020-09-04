package com.tiagohs.domain.services.config

abstract class BaseService(val retrofitConfig: RetrofitConfig) {

    fun <T> buildTMDB3Service(mClass: Class<T>): T {
        return retrofitConfig.tmdb3Build().create(mClass)
    }

    fun <T> buildTMDB4Service(mClass: Class<T>): T {
        return retrofitConfig.tmdb4Build().create(mClass)
    }

    fun <T> buildLocalService(mClass: Class<T>): T {
        return retrofitConfig.localBuild().create(mClass)
    }

    fun <T> buildOMDBService(mClass: Class<T>): T {
        return retrofitConfig.omdbBuild().create(mClass)
    }
}