package com.tiagohs.cinema_history.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.tiagohs.cinema_history.BuildConfig
import timber.log.Timber
import java.util.Calendar

/**
 * Intersticial na troca de capítulo (transição natural de conteúdo), com limites conservadores:
 *  - nunca na primeira sessão do app;
 *  - nunca nas primeiras trocas de capítulo da leitura ([AdsConfig.interstitialSkipFirstChapters]);
 *  - no máximo 1 a cada [AdsConfig.interstitialMinIntervalMs] e [AdsConfig.interstitialDailyCap] por dia.
 * O AdMob também aplica o limite de frequência configurado no bloco.
 */
class ChapterInterstitial(private val activity: Activity) {

    private var interstitial: InterstitialAd? = null
    private var loading = false
    private var chapterChanges = 0

    fun preload() {
        if (loading || interstitial != null || !canEverShow()) return

        loading = true
        InterstitialAd.load(
            activity,
            BuildConfig.ADMOB_INTERSTITIAL_CHAPTER,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    loading = false
                    interstitial = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading = false
                    Timber.d("Interstitial: %s", error.message)
                }
            }
        )
    }

    /** Chamar quando o usuário muda de capítulo. */
    fun onChapterChanged() {
        chapterChanges++

        val ad = interstitial
        if (ad == null || !canShowNow() || activity.isFinishing || activity.isDestroyed) {
            preload()
            return
        }

        interstitial = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                AdsHistory.recordInterstitial(activity)
            }

            override fun onAdDismissedFullScreenContent() {
                preload()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Timber.d("Interstitial show: %s", error.message)
                preload()
            }
        }
        ad.show(activity)
    }

    private fun canEverShow(): Boolean =
        AdsConfig.interstitialEnabled &&
            AdsManager.canRequestAds(activity) &&
            AdsHistory.sessions(activity) > 1 &&
            AdsHistory.interstitialsToday(activity) < AdsConfig.interstitialDailyCap

    private fun canShowNow(): Boolean =
        canEverShow() &&
            chapterChanges > AdsConfig.interstitialSkipFirstChapters &&
            System.currentTimeMillis() - AdsHistory.lastInterstitial(activity) >= AdsConfig.interstitialMinIntervalMs
}

/** Histórico local usado pelos limites de frequência. */
object AdsHistory {

    private const val PREFS = "ads_history"
    private const val SESSIONS = "sessions"
    private const val LAST_INTERSTITIAL = "last_interstitial"
    private const val DAY = "day"
    private const val DAY_COUNT = "day_count"

    private fun prefs(context: Context) = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Chamado uma vez por abertura do app (processo). */
    fun registerSession(context: Context) {
        val prefs = prefs(context)
        prefs.edit().putInt(SESSIONS, prefs.getInt(SESSIONS, 0) + 1).apply()
    }

    fun sessions(context: Context): Int = prefs(context).getInt(SESSIONS, 0)

    fun lastInterstitial(context: Context): Long = prefs(context).getLong(LAST_INTERSTITIAL, 0L)

    fun interstitialsToday(context: Context): Int {
        val prefs = prefs(context)
        return if (prefs.getInt(DAY, 0) == today()) prefs.getInt(DAY_COUNT, 0) else 0
    }

    fun recordInterstitial(context: Context) {
        val count = interstitialsToday(context) + 1
        prefs(context).edit()
            .putLong(LAST_INTERSTITIAL, System.currentTimeMillis())
            .putInt(DAY, today())
            .putInt(DAY_COUNT, count)
            .apply()
    }

    private fun today(): Int = Calendar.getInstance().let { it.get(Calendar.YEAR) * 1000 + it.get(Calendar.DAY_OF_YEAR) }
}
