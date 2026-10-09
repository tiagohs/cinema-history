package com.tiagohs.cinema_history.presentation.configs

import android.app.Activity
import android.content.res.Configuration
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.tiagohs.cinema_history.R
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.helpers.utils.ContentLanguage
import java.util.Locale

/**
 * Idioma (chip PT/EN/ES) e tema (claro/escuro) visíveis na toolbar. Usado na Home e nos capítulos;
 * o menu precisa ter os itens action_language (actionLayout view_home_language_action) e action_theme.
 */
object QuickSettingsMenu {

    fun bind(activity: Activity, menu: Menu) {
        menu.findItem(R.id.action_language)?.let { item ->
            val languages = ContentLanguage.ENABLED
            if (languages.size < 2) {
                item.isVisible = false
            } else {
                val current = ContentLanguage.current()
                val chip = item.actionView?.findViewById<TextView>(R.id.languageChip)
                chip?.text = current.uppercase()
                val label = activity.getString(R.string.action_language, Locale(current).let { it.getDisplayLanguage(it) })
                item.title = label
                chip?.contentDescription = label
                chip?.setOnClickListener { switchToNextLanguage(languages, current) }
            }
        }
        menu.findItem(R.id.action_theme)?.setIcon(
            if (isNight(activity)) R.drawable.ic_light_mode_white_24dp else R.drawable.ic_dark_mode_white_24dp
        )
    }

    /** true se tratou o item. */
    fun onItemSelected(activity: Activity, item: MenuItem, settingsManager: SettingsManager): Boolean {
        if (item.itemId != R.id.action_theme) return false
        toggleTheme(activity, settingsManager)
        return true
    }

    /** Alterna a partir do que está na tela agora (sai do "padrão do sistema"). */
    fun toggleTheme(activity: Activity, settingsManager: SettingsManager) {
        settingsManager.setThemeMode(if (isNight(activity)) SettingsManager.THEME_LIGHT else SettingsManager.THEME_DARK)
    }

    fun isNight(activity: Activity): Boolean =
        (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    /** PT → EN → ES → PT… (o AndroidX salva a escolha e recria as telas no novo idioma). */
    private fun switchToNextLanguage(languages: List<String>, current: String) {
        val next = languages[(languages.indexOf(current).coerceAtLeast(0) + 1) % languages.size]
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(next))
    }
}
