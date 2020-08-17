package com.tiagohs.domain.managers

import android.content.Context
import android.content.SharedPreferences

class SettingsManager:
    SharedPreferences.OnSharedPreferenceChangeListener {

    val APP_LANGUAGE_DEFAULT = "Português Brasil"
    val MOVIE_LANGUAGE_DEFAULT = "Inglês"

    private val APP_LANGUAGE_KEY = "APP_LANGUAGE_KEY"
    private val MOVIE_LANGUAGE_KEY = "MOVIE_LANGUAGE_KEY"

    private var PRIVATE_MODE = 0
    private val PREF_SETTINGS_NAME = "settings"

    var sharedPreferences: SharedPreferences? = null

    fun setupSharedPreferences(context: Context?) {
        sharedPreferences = context?.getSharedPreferences(PREF_SETTINGS_NAME, PRIVATE_MODE)
    }

    fun getAppLanguage(): String = sharedPreferences?.getString(APP_LANGUAGE_KEY, APP_LANGUAGE_DEFAULT) ?: APP_LANGUAGE_DEFAULT

    fun getAppFormatedLanguage(): String {
        val language = getAppLanguage().split("-")[1]

        return getISOLanguage(language) ?: ""
    }

    fun updateAppLanguage(newLanguage: String) {
        val sharedEdit = sharedPreferences?.edit() ?: return

        sharedEdit.putString(APP_LANGUAGE_KEY, newLanguage)
        sharedEdit.apply()
    }

    fun registerOnSharedPreferenceChangeListener() {
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    fun unregisterOnSharedPreferenceChangeListener() {
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    fun getMovieLanguage(): String = sharedPreferences?.getString(MOVIE_LANGUAGE_KEY, MOVIE_LANGUAGE_DEFAULT) ?: MOVIE_LANGUAGE_DEFAULT

    fun getMovieISOLanguage(): String {
        val language = getMovieLanguage()

        return getISOLanguage(language) ?: ""
    }

    fun updateMovieLanguage(newLanguage: String) {
        val sharedEdit = sharedPreferences?.edit() ?: return

        sharedEdit.putString(MOVIE_LANGUAGE_KEY, newLanguage)
        sharedEdit.apply()
    }

    private fun getISOLanguage(language: String): String? = when (language) {
        "Português Brasil" -> "pt-BR"
        "Inglês" -> "en-US"
        else -> "pt-BR"
    }

}