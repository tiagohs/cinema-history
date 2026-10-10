package com.tiagohs.cinema_history.presentation.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.tiagohs.helpers.utils.ContentLanguage
import java.util.Locale
import com.tiagohs.cinema_history.App
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.AdsManager
import com.tiagohs.cinema_history.support.Supporter
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.cinema_history.presentation.activities.AboutActivty
import com.tiagohs.cinema_history.presentation.activities.GlossaryActivity
import com.tiagohs.cinema_history.presentation.activities.ReferenceActivity
import javax.inject.Inject

class SettingPreferenceFragment: PreferenceFragmentCompat() {

    @Inject
    lateinit var settingManager: SettingsManager

    private val APP_LANGUAGE_KEY = "language_app"
    private val LANGUAGE_CATEGORY_KEY = "language_category"
    private val ABOUT_KEY = "about"
    private val REFERENCES_KEY = "references"
    private val GLOSSARY_KEY = "glossary"
    private val AD_PRIVACY_KEY = "ad_privacy"
    private val THEME_KEY = "app_theme"
    private val SUPPORT_KEY = "support"

    private var appLanguage: ListPreference? = null
    private var aboutLanguage: Preference? = null
    private var referencesLanguage: Preference? = null
    private var glossaryLanguage: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey)

        (activity?.application as? App)?.appComponent?.inject(this)

        setupPreferences()
    }

    private fun setupPreferences() {
        appLanguage = findPreference(APP_LANGUAGE_KEY)
        aboutLanguage = findPreference(ABOUT_KEY)
        referencesLanguage = findPreference(REFERENCES_KEY)
        glossaryLanguage = findPreference(GLOSSARY_KEY)

        setupLanguagePreference()
        setupThemePreference()

        aboutLanguage?.intent = AboutActivty.newIntent(context)
        referencesLanguage?.intent = ReferenceActivity.newIntent(context)
        glossaryLanguage?.intent = GlossaryActivity.newIntent(context)

        setupAdPrivacyPreference()
        setupSupportPreference()
    }

    /** "Apoie o app": só com a oferta disponível (Brasil + pt) ou para quem já apoia. */
    private fun setupSupportPreference() {
        val preference = findPreference<Preference>(SUPPORT_KEY) ?: return
        val activity = activity ?: return
        val supporter = Supporter.isSupporter(activity)

        preference.isVisible = supporter || Supporter.isOfferAvailable(activity)
        preference.setTitle(if (supporter) R.string.support_menu_supporter else R.string.support_menu)
        preference.setSummary(if (supporter) R.string.support_settings_summary_supporter else R.string.support_settings_summary)
        preference.setOnPreferenceClickListener {
            Supporter.openSupportScreen(activity, "settings")
            true
        }
    }

    /**
     * Tema: Claro / Escuro / Padrão do sistema (padrão). Aplicado na hora com
     * AppCompatDelegate.setDefaultNightMode (as telas abertas são recriadas no novo tema).
     */
    private fun setupThemePreference() {
        val preference = findPreference<ListPreference>(THEME_KEY) ?: return

        preference.value = settingManager.getThemeMode()
        preference.setOnPreferenceChangeListener { _, newValue ->
            settingManager.setThemeMode(newValue as String)
            true
        }
    }

    /** "Privacidade de anúncios": reabre o formulário de consentimento (exigido pelo GDPR). */
    private fun setupAdPrivacyPreference() {
        val preference = findPreference<Preference>(AD_PRIVACY_KEY) ?: return
        val activity = activity ?: return

        preference.isVisible = AdsManager.isPrivacyOptionsRequired(activity)
        preference.setOnPreferenceClickListener {
            AdsManager.showPrivacyOptions(activity)
            true
        }
    }

    /**
     * Idioma do app (interface + conteúdo + dados do TMDB), salvo pelo AndroidX (AppCompatDelegate)
     * e integrado ao seletor de idioma por app do Android 13+.
     * Só aparece quando houver mais de um idioma com conteúdo liberado (ContentLanguage.ENABLED).
     */
    private fun setupLanguagePreference() {
        val languages = ContentLanguage.ENABLED
        val preference = appLanguage ?: return

        if (languages.size < 2) {
            findPreference<PreferenceCategory>(LANGUAGE_CATEGORY_KEY)?.isVisible = false
            return
        }

        val systemOption = ""
        preference.entryValues = (listOf(systemOption) + languages).toTypedArray()
        preference.entries = (listOf(getString(R.string.language_system)) + languages.map { displayName(it) }).toTypedArray()

        val selected = AppCompatDelegate.getApplicationLocales()[0]?.language
        preference.value = selected?.takeIf { it in languages } ?: systemOption

        preference.setOnPreferenceChangeListener { _, newValue ->
            val tag = newValue as String
            val locales = if (tag.isEmpty()) LocaleListCompat.getEmptyLocaleList() else LocaleListCompat.forLanguageTags(tag)

            // Recria as telas no novo idioma; a escolha é persistida automaticamente (autoStoreLocales).
            AppCompatDelegate.setApplicationLocales(locales)
            true
        }
    }

    private fun displayName(language: String): String {
        val locale = Locale(language)
        return locale.getDisplayLanguage(locale).replaceFirstChar { it.titlecase(locale) }
    }

    override fun onResume() {
        super.onResume()

        settingManager.registerOnSharedPreferenceChangeListener()
        setupSupportPreference()
    }

    override fun onPause() {
        super.onPause()

        settingManager.unregisterOnSharedPreferenceChangeListener()
    }

}