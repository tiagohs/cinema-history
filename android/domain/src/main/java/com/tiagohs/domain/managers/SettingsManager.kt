package com.tiagohs.domain.managers

import android.content.Context
import android.content.SharedPreferences
import com.tiagohs.helpers.utils.ContentLanguage
import javax.inject.Inject

/**
 * Configurações do app.
 *
 * O idioma não é mais guardado aqui: a escolha fica com o AndroidX (AppCompatDelegate.setApplicationLocales)
 * e a decisão final do idioma do conteúdo/TMDB é do ContentLanguage.
 */
class SettingsManager
@Inject constructor(
    val context: Context,
    val sharedPreferences: SharedPreferences?
): SharedPreferences.OnSharedPreferenceChangeListener {

    fun registerOnSharedPreferenceChangeListener() {
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    fun unregisterOnSharedPreferenceChangeListener() {
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    /**
     * Idioma usado para títulos/sinopses de filmes e nas chamadas ao TMDB, como tag (ex.: "pt-BR").
     * Segue o idioma do conteúdo do app (ContentLanguage).
     */
    fun getMovieLanguage(): String = ContentLanguage.tmdbTag()

    fun getMovieISOLanguage(): String = ContentLanguage.tmdbTag()
}
