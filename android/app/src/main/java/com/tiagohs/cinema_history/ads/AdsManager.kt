package com.tiagohs.cinema_history.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.tiagohs.cinema_history.support.Supporter
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Consentimento (UMP: GDPR / estados dos EUA) e inicialização do AdMob.
 *
 * Nenhum anúncio é pedido antes de [canRequestAds] ser verdadeiro. O formulário de consentimento
 * só aparece para quem precisa (ex.: Europa); nos demais países o fluxo é transparente.
 */
object AdsManager {

    private val initialized = AtomicBoolean(false)

    private fun consentInformation(context: Context): ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context.applicationContext)

    fun canRequestAds(context: Context): Boolean =
        !Supporter.isSupporter(context) &&
            runCatching { consentInformation(context).canRequestAds() }.getOrDefault(false)

    /** Chamado na tela inicial: atualiza o consentimento, mostra o formulário se necessário e inicializa o SDK. */
    fun gatherConsent(activity: Activity, onFinished: () -> Unit = {}) {
        // Quem apoia não vê anúncios: nem consentimento nem inicialização do SDK.
        if (Supporter.isSupporter(activity)) {
            onFinished()
            return
        }

        val info = consentInformation(activity)
        val params = ConsentRequestParameters.Builder().build()

        info.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                    error?.let { Timber.w("Consent form: %s", it.message) }
                    if (info.canRequestAds()) initialize(activity)
                    onFinished()
                }
            },
            { error ->
                Timber.w("Consent update: %s", error.message)
                if (info.canRequestAds()) initialize(activity)
                onFinished()
            }
        )

        // Consentimento já obtido em uma sessão anterior: não precisa esperar a atualização.
        if (info.canRequestAds()) initialize(activity)
    }

    private fun initialize(context: Context) {
        if (Supporter.isSupporter(context)) return
        if (!initialized.compareAndSet(false, true)) return

        val appContext = context.applicationContext
        AdsConfig.fetch()
        Thread { MobileAds.initialize(appContext) {} }.start()
    }

    /** Usuário em região que exige o botão "Opções de privacidade" (ex.: GDPR). */
    fun isPrivacyOptionsRequired(context: Context): Boolean = runCatching {
        consentInformation(context).privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }.getOrDefault(false)

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            error?.let { Timber.w("Privacy options: %s", it.message) }
        }
    }
}
