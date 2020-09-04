package com.tiagohs.cinema_history.dagger.modules

import com.tiagohs.domain.services.LocalService
import com.tiagohs.domain.services.OMDBService
import com.tiagohs.domain.services.TMDBService
import com.tiagohs.domain.services.config.RetrofitConfig
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {

    @Provides
    fun provideLocalService(retrofitConfig: RetrofitConfig): LocalService {
        return LocalService(retrofitConfig)
    }

    @Provides
    fun provideTMDBService(retrofitConfig: RetrofitConfig): TMDBService {
        return TMDBService(retrofitConfig)
    }

    @Provides
    fun provideOMDBService(retrofitConfig: RetrofitConfig): OMDBService {
        return OMDBService(retrofitConfig)
    }

}