package com.tiagohs.domain.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.tiagohs.helpers.Constants
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

    /**
     * Tema do app: [THEME_LIGHT], [THEME_DARK] ou [THEME_SYSTEM] (padrão: segue o sistema).
     */
    fun getThemeMode(): String = readThemeMode(sharedPreferences)

    /** Salva o tema escolhido e aplica na hora (as telas abertas são recriadas pelo AppCompat). */
    fun setThemeMode(mode: String) {
        val value = mode.takeIf { it in THEME_MODES } ?: THEME_SYSTEM
        sharedPreferences?.edit()?.putString(KEY_THEME_MODE, value)?.apply()
        applyThemeMode(value)
    }

    companion object {
        private const val KEY_LAST_READ_ERA_ID = "home_last_read_era_id"
        private const val NO_ERA = -1

        const val KEY_THEME_MODE = "app_theme"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
        val THEME_MODES = listOf(THEME_SYSTEM, THEME_LIGHT, THEME_DARK)

        /**
         * Aplica o tema salvo. Chamado no App.onCreate, antes de qualquer Activity, para a
         * primeira tela já abrir no tema certo (sem piscar).
         */
        fun applySavedThemeMode(context: Context) {
            val preferences = context.getSharedPreferences(
                Constants.SHARED_PREFERENCES.PREF_SETTINGS_NAME,
                Constants.SHARED_PREFERENCES.PRIVATE_MODE
            )
            applyThemeMode(readThemeMode(preferences))
        }

        fun applyThemeMode(mode: String) {
            val nightMode = nightModeFor(mode)
            if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }
        }

        fun nightModeFor(mode: String): Int = when (mode) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        private fun readThemeMode(preferences: SharedPreferences?): String =
            preferences?.getString(KEY_THEME_MODE, THEME_SYSTEM)?.takeIf { it in THEME_MODES } ?: THEME_SYSTEM
    }
}
