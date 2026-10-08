package com.tiagohs.cinema_history.ads

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.tiagohs.cinema_history.BuildConfig
import timber.log.Timber

/**
 * Limites dos anúncios, ajustáveis pelo Firebase Remote Config sem publicar nova versão.
 * Os valores abaixo são os padrões (usados offline ou se o Remote Config falhar).
 */
object AdsConfig {

    const val NATIVE_ENABLED = "ads_native_enabled"
    const val INTERSTITIAL_ENABLED = "ads_interstitial_enabled"
    const val INTERSTITIAL_MIN_INTERVAL_SEC = "ads_interstitial_min_interval_sec"
    const val INTERSTITIAL_DAILY_CAP = "ads_interstitial_daily_cap"
    const val INTERSTITIAL_SKIP_FIRST_CHAPTERS = "ads_interstitial_skip_first_chapters"

    private val DEFAULTS: Map<String, Any> = mapOf(
        NATIVE_ENABLED to true,
        INTERSTITIAL_ENABLED to true,
        INTERSTITIAL_MIN_INTERVAL_SEC to 240L,
        INTERSTITIAL_DAILY_CAP to 6L,
        INTERSTITIAL_SKIP_FIRST_CHAPTERS to 2L
    )

    private val remoteConfig: FirebaseRemoteConfig? by lazy {
        runCatching {
            FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(
                    FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 60 else 12 * 60 * 60)
                        .build()
                )
                setDefaultsAsync(DEFAULTS)
            }
        }.onFailure { Timber.w(it, "Remote Config indisponível") }.getOrNull()
    }

    fun fetch() {
        remoteConfig?.fetchAndActivate()?.addOnFailureListener { Timber.w(it, "Remote Config fetch") }
    }

    val nativeEnabled: Boolean get() = bool(NATIVE_ENABLED)
    val interstitialEnabled: Boolean get() = bool(INTERSTITIAL_ENABLED)
    val interstitialMinIntervalMs: Long get() = long(INTERSTITIAL_MIN_INTERVAL_SEC) * 1000
    val interstitialDailyCap: Long get() = long(INTERSTITIAL_DAILY_CAP)
    val interstitialSkipFirstChapters: Long get() = long(INTERSTITIAL_SKIP_FIRST_CHAPTERS)

    private fun bool(key: String): Boolean =
        remoteConfig?.getBoolean(key) ?: (DEFAULTS[key] as Boolean)

    private fun long(key: String): Long =
        remoteConfig?.getLong(key) ?: (DEFAULTS[key] as Long)
}
