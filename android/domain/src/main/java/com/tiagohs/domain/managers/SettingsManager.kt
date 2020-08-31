package com.tiagohs.domain.managers

import android.content.Context
import android.content.SharedPreferences
import com.tiagohs.domain.services.LocalService
import com.tiagohs.helpers.R
import com.tiagohs.helpers.extensions.getResourceString
import javax.inject.Inject

class SettingsManager
@Inject constructor(
    val context: Context,
    val sharedPreferences: SharedPreferences
): SharedPreferences.OnSharedPreferenceChangeListener {

    val APP_LANGUAGE_DEFAULT = "Português Brasil"
    val MOVIE_LANGUAGE_DEFAULT = "Inglês"

    private val APP_LANGUAGE_KEY = "APP_LANGUAGE_KEY"
    private val MOVIE_LANGUAGE_KEY = "MOVIE_LANGUAGE_KEY"

    fun getAppLanguage(): String = sharedPreferences?.getString(APP_LANGUAGE_KEY, APP_LANGUAGE_DEFAULT) ?: context.getResourceString(R.string.pt_br_country_name)

    fun getAppFormatedLanguage(): String = getAppLanguage().split("-").getOrNull(1) ?: ""

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

    fun getMovieLanguage(): String = sharedPreferences?.getString(MOVIE_LANGUAGE_KEY, MOVIE_LANGUAGE_DEFAULT) ?: context.getResourceString(R.string.en_us_country_name)

    fun getMovieISOLanguage(): String = getMovieLanguage()

    fun updateMovieLanguage(newLanguage: String) {
        val sharedEdit = sharedPreferences?.edit() ?: return

        sharedEdit.putString(MOVIE_LANGUAGE_KEY, newLanguage)
        sharedEdit.apply()
    }

    fun getISOLanguage(language: String): String? = when (language) {
        context.getString(R.string.pt_br_country_name) -> "pt-BR"
        context.getString(R.string.en_us_country_name) -> "en-US"
        else -> "pt-BR"
    }

    fun getNameLanguage(language: String): String? = when (language) {
        "pt-BR" -> context.getString(R.string.pt_br_country_name)
        "en-US" -> context.getString(R.string.en_us_country_name)
        else -> context.getString(R.string.en_us_country_name)
    }
}