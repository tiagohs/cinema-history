package com.tiagohs.cinema_history.presentation.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tiagohs.cinema_history.R
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.cinema_history.presentation.activities.AboutActivty
import com.tiagohs.cinema_history.presentation.activities.ReferenceActivity

class SettingPreferenceFragment: PreferenceFragmentCompat() {

    private val APP_LANGUAGE_KEY = "language_app"
    private val MOVIE_LANGUAGE_KEY = "language_movies"
    private val ABOUT_KEY = "about"
    private val REFERENCES_KEY = "references"

    private lateinit var settingManager: SettingsManager
    private var appLanguage: ListPreference? = null
    private var moviesLanguage: ListPreference? = null
    private var aboutLanguage: Preference? = null
    private var referencesLanguage: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey)

        setupSettingsManager()
        setupPreferences()
    }

    private fun setupSettingsManager() {
        settingManager = SettingsManager()
        settingManager.setupSharedPreferences(context)
    }

    private fun setupPreferences() {
        appLanguage = findPreference(APP_LANGUAGE_KEY)
        moviesLanguage = findPreference(MOVIE_LANGUAGE_KEY)
        aboutLanguage = findPreference(ABOUT_KEY)
        referencesLanguage = findPreference(REFERENCES_KEY)

        appLanguage?.summary = settingManager.getAppLanguage()
        appLanguage?.setOnPreferenceChangeListener { preference, newLanguage ->
            settingManager.updateAppLanguage(newLanguage as String)
            preference.summary = settingManager.getAppLanguage()

            true
        }
        moviesLanguage?.summary = settingManager.getMovieLanguage()
        moviesLanguage?.setOnPreferenceChangeListener { preference, newLanguage ->
            settingManager.updateMovieLanguage(newLanguage as String)
            preference.summary = settingManager.getMovieLanguage()

            true
        }

        aboutLanguage?.intent = AboutActivty.newIntent(context)
        referencesLanguage?.intent = ReferenceActivity.newIntent(context)
    }

    override fun onResume() {
        super.onResume()

        settingManager.registerOnSharedPreferenceChangeListener()
    }

    override fun onPause() {
        super.onPause()

        settingManager.unregisterOnSharedPreferenceChangeListener()
    }

}