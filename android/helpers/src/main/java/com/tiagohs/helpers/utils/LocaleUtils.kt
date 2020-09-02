@file:Suppress("DEPRECATION")

package com.tiagohs.helpers.utils

import android.content.res.Resources
import com.tiagohs.helpers.R

import java.util.ArrayList
import java.util.Collections
import java.util.Locale

object LocaleUtils {

    val localeAtual: Locale
        get() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            Resources.getSystem().configuration.locales.get(0)
        else
            Resources.getSystem().configuration.locale

    val localeLanguageISO: String
        get() = localeAtual.language

    val localeLanguageName: String
        get() = localeAtual.displayLanguage

    val localeCountryISO: String
        get() = localeAtual.country

    val localeCountryName: String
        get() = localeAtual.displayCountry

    val localeLanguageAndCountry: String
        get() = "$localeLanguageISO-$localeCountryISO"

    val allCountrys: List<String>
        get() {
            val locales = Locale.getAvailableLocales()
            val countries = ArrayList<String>()
            for (locale in locales) {
                val country = locale.displayCountry
                if (country.trim { it <= ' ' }.length > 0 && !countries.contains(country)) {
                    countries.add(country)
                }
            }

            Collections.sort(countries)

            return countries
        }

    fun getLocaleLanguageName(languageIso: String): String {
        return Locale("en").getDisplayLanguage(Locale(languageIso))
    }

    fun getLocaleCountryISO(locale: Locale): String {
        return locale.country
    }

    fun getCountryName(languageIso: String?): Int {
        return when (languageIso) {
            "pt-BR" -> R.string.brazil
            "en-US" -> R.string.eua
            else -> R.string.eua
        }
    }


    fun getLanguageName(languageIso: String?): String? {
        val all = Locale.getAvailableLocales()
        for (locale in all) {
            val language = locale.language
            if (language == languageIso) {
                return locale.displayLanguage
            }
        }

        return null
    }

    fun getLocaleLanguageAndCountry(locale: Locale): String {
        return localeAtual.language + "-" + getLocaleCountryISO(locale)
    }

}
