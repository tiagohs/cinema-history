package com.tiagohs.cinema_history.support

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.analytics.FirebaseAnalytics
import com.tiagohs.helpers.utils.ContentLanguage
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Contrato do "Apoie o Cinema History" (compra única, para sempre), usado pelo resto do app.
 * Implementação: compra pelo Google Play Billing (ver SupportActivity / SupportBilling).
 *
 * - isSupporter: o usuário comprou (qualquer um dos valores) → sem anúncios + áudio liberado.
 * - isOfferAvailable: a oferta (e o áudio) só existe no Brasil com o app em português.
 */
object Supporter {

    /** País da conta do Google Play (BillingConfig) em que a oferta existe. */
    const val OFFER_COUNTRY = "BR"

    private val listeners = CopyOnWriteArrayList<() -> Unit>()
    private val main = Handler(Looper.getMainLooper())

    /**
     * Chamado uma vez no App.onCreate: prepara o Billing e reconfirma o apoio/país no Play ao abrir
     * o app e sempre que ele volta para o primeiro plano.
     */
    fun init(application: Application) {
        SupportBilling.init(application)
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            private var started = 0

            override fun onActivityStarted(activity: Activity) {
                if (started++ == 0) SupportBilling.refresh()
            }

            override fun onActivityStopped(activity: Activity) {
                started = (started - 1).coerceAtLeast(0)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    /** Já apoiou (compra PURCHASED reconfirmada pelo Play; cache local em SharedPreferences). */
    fun isSupporter(context: Context): Boolean = SupportStore.isSupporter(context)

    /** A oferta existe para este usuário: conta do Play no Brasil e app em português. */
    fun isOfferAvailable(context: Context): Boolean {
        if (SupportStore.debugForceOffer(context)) return true
        return SupportStore.country(context) == OFFER_COUNTRY &&
            ContentLanguage.current() == ContentLanguage.PORTUGUESE
    }

    /** Pagamento (Pix/boleto) aguardando confirmação do Play. */
    fun isPurchasePending(context: Context): Boolean = SupportStore.isPending(context)

    /** Abre a tela de apoio. [source] = de onde veio (onboarding, menu, audio, settings, chapter_end). */
    fun openSupportScreen(activity: Activity, source: String) {
        if (!isOfferAvailable(activity) && !isSupporter(activity)) return

        runCatching {
            FirebaseAnalytics.getInstance(activity).logEvent(
                "support_screen_open",
                Bundle().apply { putString("source", source) }
            )
        }
        activity.startActivity(SupportActivity.newIntent(activity, source))
    }

    /** Ouvintes de mudança (compra concluída/restaurada, pagamento pendente, país da conta). */
    fun addListener(listener: () -> Unit) {
        listeners.addIfAbsent(listener)
    }

    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    internal fun notifyChanged() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listeners.forEach { it() }
        } else {
            main.post { listeners.forEach { it() } }
        }
    }
}
