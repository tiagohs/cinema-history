package com.tiagohs.cinema_history.presentation.configs

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.appcompat.app.AppCompatActivity
import com.tiagohs.domain.services.LocalService
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.utils.ContentLanguage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * As telas da era (sumário e capítulos) recebem a era (título, sumário…) pronta no Intent. Ao trocar o idioma
 * dentro delas, o Android recria a tela com o mesmo Intent — ou seja, com os textos no idioma antigo.
 * Isto marca o idioma no Intent e, se ele mudou, busca a era de novo no idioma atual e reabre a tela.
 */
object LocalizedMainTopic {

    private const val EXTRA_LANG = "content_lang"

    /** Marca no Intent o idioma em que a era (passada junto) foi carregada. */
    fun tag(intent: Intent): Intent = intent.putExtra(EXTRA_LANG, ContentLanguage.current())

    /**
     * Chame no onResume. Se a era do Intent está em outro idioma, busca de novo e reabre a tela com [reopen].
     * (No onResume e não no onCreate: as telas que estavam por baixo nem sempre são recriadas.)
     */
    fun reopenIfLanguageChanged(
        activity: AppCompatActivity,
        localService: LocalService,
        topic: MainTopicItem?,
        reopen: (MainTopicItem) -> Intent
    ): Boolean {
        val tagged = activity.intent.getStringExtra(EXTRA_LANG) ?: return false
        val current = ContentLanguage.current()
        if (tagged == current || topic == null) return false
        if (activity.intent.getBooleanExtra(EXTRA_RELOADING, false)) return true
        activity.intent.putExtra(EXTRA_RELOADING, true)

        val sub: Disposable = localService.getMainTopics()
            .map { list -> list.firstOrNull { (it as? MainTopicItem)?.id == topic.id } as MainTopicItem }
            .map { fresh -> fresh.apply { sumarioList = localService.getSumarioByMainTopicID(id).blockingFirst() } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ fresh -> restart(activity, tag(reopen(fresh))) }, { activity.intent.removeExtra(EXTRA_RELOADING) })
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) = sub.dispose()
        })
        return true
    }

    private const val EXTRA_RELOADING = "content_lang_reloading"

    private fun restart(activity: Activity, intent: Intent) {
        if (activity.isFinishing || activity.isDestroyed) return
        activity.startActivity(intent)
        @Suppress("DEPRECATION")
        activity.overridePendingTransition(0, 0)
        activity.finish()
        @Suppress("DEPRECATION")
        activity.overridePendingTransition(0, 0)
    }
}
