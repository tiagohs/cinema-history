package com.tiagohs.cinema_history.dagger.modules

import com.tiagohs.cinema_history.services.LocalService
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {

    @Provides
    fun provideLocalService(): LocalService {
        return LocalService()
    }
}