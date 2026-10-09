package com.tiagohs.cinema_history

import android.app.Application
import android.content.Context
import com.tiagohs.cinema_history.ads.AdsHistory
import com.tiagohs.cinema_history.dagger.AppComponent
import com.tiagohs.cinema_history.dagger.DaggerAppComponent
import com.tiagohs.cinema_history.dagger.modules.AppModule
import com.tiagohs.helpers.utils.ContentLanguage
import timber.log.Timber
import com.tiagohs.helpers.utils.RemoteContent

class App: Application() {
    var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        configureDagger()
        configureTimber()
        configureContentLanguages()
        configureRemoteContent()

        // Anúncios: o SDK só é inicializado depois do consentimento (HomeActivity -> AdsManager).
        AdsHistory.registerSession(this)
    }

    @Suppress("DEPRECATION")
    private fun configureDagger() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    /** Conteúdo atualizável pelo site (prêmios etc.): usa o cache já baixado e atualiza em segundo plano. */
    private fun configureRemoteContent() {
        RemoteContent.init(this)
        Thread({ RemoteContent.sync(this) }, "remote-content").apply { isDaemon = true }.start()
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