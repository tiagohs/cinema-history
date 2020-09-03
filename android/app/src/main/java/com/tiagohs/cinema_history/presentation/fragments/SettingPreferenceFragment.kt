package com.tiagohs.cinema_history.presentation.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tiagohs.cinema_history.App
import com.tiagohs.cinema_history.R
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.cinema_history.presentation.activities.AboutActivty
import com.tiagohs.cinema_history.presentation.activities.ReferenceActivity
import com.tiagohs.helpers.extensions.getResourceString
import javax.inject.Inject

class SettingPreferenceFragment: PreferenceFragmentCompat() {

    @Inject
    lateinit var settingManager: SettingsManager

    private val APP_LANGUAGE_KEY = "language_app"
    private val MOVIE_LANGUAGE_KEY = "language_movies"
    private val ABOUT_KEY = "about"
    private val REFERENCES_KEY = "references"

    private var appLanguage: ListPreference? = null
    private var moviesLanguage: ListPreference? = null
    private var aboutLanguage: Preference? = null
    private var referencesLanguage: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey)

        (activity?.application as? App)?.appComponent?.inject(this)

        setupPreferences()
    }

    private fun setupPreferences() {
        appLanguage = findPreference(APP_LANGUAGE_KEY)
        moviesLanguage = findPreference(MOVIE_LANGUAGE_KEY)
        aboutLanguage = findPreference(ABOUT_KEY)
        referencesLanguage = findPreference(REFERENCES_KEY)

        appLanguage?.summary = context.getResourceString(R.string.pt_br_country_name)
        appLanguage?.setOnPreferenceChangeListener { preference, newLanguage ->
            settingManager.updateAppLanguage(newLanguage as String)
            preference.summary = context.getResourceString(R.string.pt_br_country_name)

            true
        }
        moviesLanguage?.summary = settingManager.getNameLanguage(settingManager.getMovieLanguage())
        moviesLanguage?.setOnPreferenceChangeListener { preference, newLanguage ->
            settingManager.updateMovieLanguage(newLanguage as String)
            preference.summary = settingManager.getNameLanguage(settingManager.getMovieLanguage())

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