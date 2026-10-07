package com.tiagohs.cinema_history

import android.app.Application
import android.content.Context
import com.tiagohs.cinema_history.dagger.AppComponent
import com.tiagohs.cinema_history.dagger.DaggerAppComponent
import com.tiagohs.cinema_history.dagger.modules.AppModule
import com.tiagohs.helpers.utils.ContentLanguage
import timber.log.Timber

class App: Application() {
    var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        configureDagger()
        configureTimber()
        configureContentLanguages()

        // MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
    }

    @Suppress("DEPRECATION")
    private fun configureDagger() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private fun configureTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    /** Em debug, inglês e espanhol ficam disponíveis em Configurações para revisar a tradução. */
    private fun configureContentLanguages() {
        if (BuildConfig.DEBUG) {
            ContentLanguage.enablePreview(listOf(ContentLanguage.ENGLISH, ContentLanguage.SPANISH))
        }
    }

    companion object {

        var appContext: Context? = null

    }
}