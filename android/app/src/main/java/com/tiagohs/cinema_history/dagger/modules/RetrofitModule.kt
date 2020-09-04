package com.tiagohs.cinema_history.dagger.modules

import android.content.Context
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.domain.services.config.RetrofitConfig
import com.tiagohs.helpers.network.FakeInterceptor
import dagger.Module
import dagger.Provides


@Module
class RetrofitModule {

    @Provides
    fun providerFakeInterceptor(context: Context): FakeInterceptor {
        return FakeInterceptor(context)
    }

    @Provides
    fun providerRetrofitConfig(context: Context): RetrofitConfig {
        return RetrofitConfig(context, BuildConfig.THEMOVIEDB_API_KEY, BuildConfig.OMDB_API_KEY)
    }
}