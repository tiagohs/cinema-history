package com.tiagohs.cinema_history.support

import android.content.Context
import android.content.SharedPreferences
import com.tiagohs.cinema_history.BuildConfig

/**
 * Estado local do apoio (SharedPreferences). É só um cache: o Google Play é a fonte da verdade e
 * reconfirma tudo a cada abertura/volta ao app ([SupportBilling.refresh]).
 */
internal object SupportStore {

    private const val PREFS = "support"

    private const val KEY_SUPPORTER = "supporter"
    private const val KEY_PRODUCT = "supporter_product"
    private const val KEY_PENDING = "purchase_pending"
    private const val KEY_COUNTRY = "billing_country"

    // Cartão "apoie" no fim dos capítulos.
    private const val KEY_COMPLETED_CHAPTERS = "completed_chapters"
    private const val KEY_LAST_CARD_MILESTONE = "chapter_card_milestone"

    // Só em builds de debug (opção escondida em Sobre: 7 toques na versão).
    private const val KEY_DEBUG_FORCE_OFFER = "debug_force_offer"
    private const val KEY_DEBUG_FORCE_SUPPORTER = "debug_force_supporter"
    private const val KEY_DEBUG_FORCE_PENDING = "debug_force_pending"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ------------------------------------------------------------------ Compra

    fun isSupporter(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SUPPORTER, false) || debugForceSupporter(context)

    fun supporterProduct(context: Context): String? = prefs(context).getString(KEY_PRODUCT, null)

    /** Grava o resultado reconfirmado pelo Play. Devolve true se algo mudou. */
    fun setSupporter(context: Context, supporter: Boolean, productId: String?): Boolean {
        val prefs = prefs(context)
        val changed = prefs.getBoolean(KEY_SUPPORTER, false) != supporter ||
            (supporter && prefs.getString(KEY_PRODUCT, null) != productId)
        if (changed) {
            prefs.edit()
                .putBoolean(KEY_SUPPORTER, supporter)
                .putString(KEY_PRODUCT, if (supporter) productId else null)
                .apply()
        }
        return changed
    }

    fun isPending(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PENDING, false) || debugForcePending(context)

    fun setPending(context: Context, pending: Boolean): Boolean {
        val prefs = prefs(context)
        if (prefs.getBoolean(KEY_PENDING, false) == pending) return false
        prefs.edit().putBoolean(KEY_PENDING, pending).apply()
        return true
    }

    // ------------------------------------------------------------------ País (BillingConfig)

    fun country(context: Context): String? = prefs(context).getString(KEY_COUNTRY, null)

    fun setCountry(context: Context, countryCode: String?): Boolean {
        val value = countryCode?.uppercase()?.takeIf { it.isNotBlank() } ?: return false
        val prefs = prefs(context)
        if (prefs.getString(KEY_COUNTRY, null) == value) return false
        prefs.edit().putString(KEY_COUNTRY, value).apply()
        return true
    }

    // ------------------------------------------------------------------ Capítulos concluídos

    fun completedChapters(context: Context): Int =
        prefs(context).getStringSet(KEY_COMPLETED_CHAPTERS, emptySet())?.size ?: 0

    /** Marca o capítulo como concluído (uma vez por capítulo). Devolve true se era novo. */
    fun markChapterCompleted(context: Context, chapterKey: String): Boolean {
        val prefs = prefs(context)
        val current = prefs.getStringSet(KEY_COMPLETED_CHAPTERS, emptySet()) ?: emptySet()
        if (chapterKey in current) return false
        prefs.edit().putStringSet(KEY_COMPLETED_CHAPTERS, HashSet(current) + chapterKey).apply()
        return true
    }

    fun lastCardMilestone(context: Context): Int = prefs(context).getInt(KEY_LAST_CARD_MILESTONE, 0)

    fun setLastCardMilestone(context: Context, milestone: Int) {
        prefs(context).edit().putInt(KEY_LAST_CARD_MILESTONE, milestone).apply()
    }

    // ------------------------------------------------------------------ Debug

    fun debugForceOffer(context: Context): Boolean =
        BuildConfig.DEBUG && prefs(context).getBoolean(KEY_DEBUG_FORCE_OFFER, false)

    fun debugForceSupporter(context: Context): Boolean =
        BuildConfig.DEBUG && prefs(context).getBoolean(KEY_DEBUG_FORCE_SUPPORTER, false)

    fun debugForcePending(context: Context): Boolean =
        BuildConfig.DEBUG && prefs(context).getBoolean(KEY_DEBUG_FORCE_PENDING, false)

    fun setDebugFlags(context: Context, forceOffer: Boolean, forceSupporter: Boolean, forcePending: Boolean) {
        if (!BuildConfig.DEBUG) return
        prefs(context).edit()
            .putBoolean(KEY_DEBUG_FORCE_OFFER, forceOffer)
            .putBoolean(KEY_DEBUG_FORCE_SUPPORTER, forceSupporter)
            .putBoolean(KEY_DEBUG_FORCE_PENDING, forcePending)
            .apply()
    }

    fun debugResetChapterCard(context: Context) {
        if (!BuildConfig.DEBUG) return
        prefs(context).edit().remove(KEY_COMPLETED_CHAPTERS).remove(KEY_LAST_CARD_MILESTONE).apply()
    }
}
