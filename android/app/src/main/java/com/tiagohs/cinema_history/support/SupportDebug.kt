package com.tiagohs.cinema_history.support

import android.app.Activity
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.onboarding.Onboarding
import com.tiagohs.helpers.utils.ContentLanguage

/**
 * Opção escondida, só em builds de DEBUG (7 toques na versão em Sobre): força a oferta/apoiador
 * para testar sem conta de teste do Play, simula pagamento pendente e reabre o onboarding.
 */
object SupportDebug {

    private const val TAPS = 7
    private var taps = 0
    private var lastTap = 0L

    /** Chamar a cada toque na versão. Abre o painel no 7º toque seguido. */
    fun onVersionTapped(activity: Activity) {
        if (!BuildConfig.DEBUG) return
        val now = System.currentTimeMillis()
        taps = if (now - lastTap < 1_500) taps + 1 else 1
        lastTap = now
        if (taps >= TAPS) {
            taps = 0
            show(activity)
        }
    }

    fun show(activity: Activity) {
        if (!BuildConfig.DEBUG) return
        val checked = booleanArrayOf(
            SupportStore.debugForceOffer(activity),
            SupportStore.debugForceSupporter(activity),
            SupportStore.debugForcePending(activity),
            false,
            false
        )
        val items = arrayOf(
            activity.getString(R.string.support_debug_force_offer),
            activity.getString(R.string.support_debug_force_supporter),
            activity.getString(R.string.support_debug_force_pending),
            activity.getString(R.string.support_debug_reset_onboarding),
            activity.getString(R.string.support_debug_reset_chapter_card)
        )
        val status = activity.getString(
            R.string.support_debug_status,
            SupportStore.country(activity) ?: "?",
            ContentLanguage.current(),
            SupportStore.isSupporter(activity).toString()
        )

        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.support_debug_title) + "\n" + status)
            .setMultiChoiceItems(items, checked) { _, which, isChecked -> checked[which] = isChecked }
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                SupportStore.setDebugFlags(activity, checked[0], checked[1], checked[2])
                if (checked[3]) Onboarding.reset(activity)
                if (checked[4]) SupportStore.debugResetChapterCard(activity)
                Supporter.notifyChanged()
                Toast.makeText(activity, "OK", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
