package com.tiagohs.cinema_history

import android.app.Application
import android.content.Context
import com.tiagohs.cinema_history.dagger.AppComponent
import com.tiagohs.cinema_history.dagger.DaggerAppComponent
import com.tiagohs.cinema_history.dagger.modules.AppModule
import timber.log.Timber

class App: Application() {
    var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        configureDagger()

        Timber.plant(Timber.DebugTree())

        // MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
    }

    @Suppress("DEPRECATION")
    private fun configureDagger() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {

        var appContext: Context? = null

    }
}