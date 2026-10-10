package com.tiagohs.cinema_history.audio.ui

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.audio.AudioConfig
import com.tiagohs.cinema_history.audio.AudioRepository

/**
 * Só em DEBUG (menu ⋮ da página do capítulo → "Áudio (debug)"): liga a fonte de teste
 * (assets/audio-test) e simula o estado do apoio, sem mexer no Supporter.
 * Textos fixos em português de propósito: não vai para o usuário final.
 */
object AudioDebugDialog {

    const val MENU_ID = 0x0A0D10

    fun show(activity: Activity) {
        if (!BuildConfig.DEBUG) return
        val test = AudioConfig.isTestSource(activity)
        val current = AudioConfig.accessOverride(activity)
        val options = arrayOf(
            (if (test) "✓ " else "") + "Fonte de teste (assets/audio-test, era 1 caps. 1–2)",
            mark(current == "") + "Apoio: comportamento real (Supporter)",
            mark(current == "hidden") + "Apoio: sem oferta (tudo escondido)",
            mark(current == "locked") + "Apoio: oferta, não apoia (cadeado, só a Abertura)",
            mark(current == "unlocked") + "Apoio: apoiador (tudo liberado)"
        )
        AlertDialog.Builder(activity)
            .setTitle("Áudio (debug)")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> AudioConfig.setTestSource(activity, !test)
                    1 -> AudioConfig.setAccessOverride(activity, "")
                    2 -> AudioConfig.setAccessOverride(activity, "hidden")
                    3 -> AudioConfig.setAccessOverride(activity, "locked")
                    4 -> AudioConfig.setAccessOverride(activity, "unlocked")
                }
                AudioRepository.clearMemory()
                activity.recreate()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun mark(selected: Boolean) = if (selected) "● " else "○ "
}
