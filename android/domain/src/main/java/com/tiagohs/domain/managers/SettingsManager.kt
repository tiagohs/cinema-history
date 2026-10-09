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

    /**
     * Última era da História do Cinema aberta pelo usuário (usada no "Continue lendo" da Home).
     */
    fun getLastReadEraId(): Int? {
        val id = sharedPreferences?.getInt(KEY_LAST_READ_ERA_ID, NO_ERA) ?: NO_ERA
        return if (id == NO_ERA) null else id
    }

    fun setLastReadEraId(eraId: Int) {
        sharedPreferences?.edit()?.putInt(KEY_LAST_READ_ERA_ID, eraId)?.apply()
    }

    companion object {
        private const val KEY_LAST_READ_ERA_ID = "home_last_read_era_id"
        private const val NO_ERA = -1
    }
}
