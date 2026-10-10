package com.tiagohs.cinema_history.support

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.GetBillingConfigParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import timber.log.Timber

/** Resultado de uma tentativa de compra, entregue à tela de apoio. */
sealed class PurchaseOutcome {
    /** Compra confirmada (PURCHASED): apoio liberado. */
    object Success : PurchaseOutcome()
    /** Pix/boleto aguardando pagamento: só libera quando virar PURCHASED. */
    object Pending : PurchaseOutcome()
    object Canceled : PurchaseOutcome()
    /** O usuário já tinha comprado: o apoio foi restaurado. */
    object AlreadyOwned : PurchaseOutcome()
    data class Error(val responseCode: Int) : PurchaseOutcome()
}

/**
 * Google Play Billing (Play Billing Library 8) para o apoio: produtos de compra única, não consumíveis.
 *
 * - Conexão única para o app, com reconexão automática (enableAutoServiceReconnection) e nova
 *   tentativa manual quando uma operação encontra o serviço desconectado.
 * - [refresh]: país da conta do Play (BillingConfig, guardado em cache) + compras existentes;
 *   chamado ao abrir o app e sempre que ele volta para o primeiro plano ([Supporter.init]).
 * - Compras PENDING (Pix/boleto) não liberam nada até virarem PURCHASED.
 * - Toda compra PURCHASED é reconhecida (acknowledge); sem isso o Play reembolsa em 3 dias.
 *
 * Todos os callbacks públicos são entregues na thread principal.
 */
internal object SupportBilling : PurchasesUpdatedListener {

    private val main = Handler(Looper.getMainLooper())

    private lateinit var appContext: Context
    private var client: BillingClient? = null
    private val waitingConnection = mutableListOf<(BillingResult) -> Unit>()

    private var lastRefresh = 0L
    private var purchaseCallback: ((PurchaseOutcome) -> Unit)? = null

    /** Detalhes já carregados (id -> produto), para a tela abrir rápido na segunda vez. */
    var cachedProducts: List<SupportProduct> = emptyList()
        private set

    fun init(context: Context) {
        if (::appContext.isInitialized) return
        appContext = context.applicationContext
    }

    private fun billingClient(): BillingClient = client ?: BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()
        .also { client = it }

    /** Garante a conexão com o Play e chama [onReady] com o resultado (OK ou erro). */
    private fun connect(onReady: (BillingResult) -> Unit) {
        val billing = billingClient()
        if (billing.isReady) {
            onReady(okResult())
            return
        }

        waitingConnection += onReady
        if (billing.connectionState == BillingClient.ConnectionState.CONNECTING) return

        runCatching {
            billing.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    main.post {
                        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                            Timber.w("Billing setup: %s %s", result.responseCode, result.debugMessage)
                        }
                        val callbacks = waitingConnection.toList()
                        waitingConnection.clear()
                        callbacks.forEach { it(result) }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // A reconexão automática cuida das próximas chamadas; aqui só registramos.
                    Timber.d("Billing service disconnected")
                }
            })
        }.onFailure {
            Timber.w(it, "Billing startConnection")
            // Cliente encerrado ou em estado inválido: recria na próxima tentativa.
            client = null
            val callbacks = waitingConnection.toList()
            waitingConnection.clear()
            callbacks.forEach { cb -> cb(errorResult(BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE)) }
        }
    }

    // ------------------------------------------------------------------ País + compras existentes

    /**
     * Reconfirma o país e as compras no Play. [force] ignora o intervalo mínimo (ex.: "Restaurar compra").
     * [onDone] recebe true se a consulta das compras funcionou.
     */
    fun refresh(force: Boolean = false, onDone: ((Boolean) -> Unit)? = null) {
        if (!::appContext.isInitialized) return
        val now = SystemClock.elapsedRealtime()
        if (!force && onDone == null && lastRefresh != 0L && now - lastRefresh < MIN_REFRESH_INTERVAL_MS) return
        lastRefresh = now

        connect { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                onDone?.invoke(false)
                return@connect
            }
            fetchCountry()
            queryPurchases(onDone)
        }
    }

    private fun fetchCountry() {
        val billing = client ?: return
        billing.getBillingConfigAsync(GetBillingConfigParams.newBuilder().build()) { result, config ->
            main.post {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (SupportStore.setCountry(appContext, config?.countryCode)) Supporter.notifyChanged()
                } else {
                    Timber.d("BillingConfig: %s %s", result.responseCode, result.debugMessage)
                }
            }
        }
    }

    private fun queryPurchases(onDone: ((Boolean) -> Unit)?) {
        val billing = client
        if (billing == null) {
            onDone?.invoke(false)
            return
        }
        val params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()

        billing.queryPurchasesAsync(params) { result, purchases ->
            main.post {
                val ok = result.responseCode == BillingClient.BillingResponseCode.OK
                if (ok) {
                    handlePurchases(purchases, authoritative = true)
                } else {
                    Timber.w("queryPurchases: %s %s", result.responseCode, result.debugMessage)
                }
                onDone?.invoke(ok)
            }
        }
    }

    /**
     * [authoritative] = lista completa vinda do Play (queryPurchases): se não houver compra
     * PURCHASED, o apoio é removido (ex.: reembolso). Atualizações parciais só concedem.
     */
    private fun handlePurchases(purchases: List<Purchase>, authoritative: Boolean): PurchaseOutcome? {
        val ours = purchases.filter { purchase -> purchase.products.any { it in SupportProducts.ALL } }
        val purchased = ours.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        val pending = ours.any { it.purchaseState == Purchase.PurchaseState.PENDING }

        purchased.filterNot { it.isAcknowledged }.forEach { acknowledge(it) }

        var changed = false
        if (purchased.isNotEmpty()) {
            val productId = purchased.flatMap { it.products }.firstOrNull { it in SupportProducts.ALL }
            changed = SupportStore.setSupporter(appContext, true, productId) or changed
        } else if (authoritative) {
            changed = SupportStore.setSupporter(appContext, false, null) or changed
        }

        if (authoritative || purchased.isNotEmpty() || pending) {
            changed = SupportStore.setPending(appContext, pending && purchased.isEmpty()) or changed
        }

        if (changed) Supporter.notifyChanged()

        return when {
            purchased.isNotEmpty() -> PurchaseOutcome.Success
            pending -> PurchaseOutcome.Pending
            else -> null
        }
    }

    private fun acknowledge(purchase: Purchase, attempt: Int = 1) {
        val billing = client ?: return
        val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billing.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Timber.w("acknowledge: %s %s", result.responseCode, result.debugMessage)
                // Nova tentativa curta; se falhar de novo, o próximo refresh (abertura/volta ao app) tenta outra vez.
                if (attempt < 3) main.postDelayed({ acknowledge(purchase, attempt + 1) }, 2_000L * attempt)
            }
        }
    }

    // ------------------------------------------------------------------ Produtos

    /** Consulta os 3 valores no Play. Devolve a lista na ordem de [SupportProducts.ALL] ou o código de erro. */
    fun queryProducts(onResult: (products: List<SupportProduct>, errorCode: Int?) -> Unit) {
        connect { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                onResult(emptyList(), result.responseCode)
                return@connect
            }
            val billing = client ?: return@connect onResult(emptyList(), BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(SupportProducts.ALL.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                })
                .build()

            billing.queryProductDetailsAsync(params) { detailsResult, queryResult ->
                main.post {
                    if (detailsResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        Timber.w("productDetails: %s %s", detailsResult.responseCode, detailsResult.debugMessage)
                        onResult(emptyList(), detailsResult.responseCode)
                        return@post
                    }
                    val byId = queryResult.productDetailsList.mapNotNull { SupportProduct.from(it) }.associateBy { it.id }
                    val products = SupportProducts.ALL.mapNotNull { byId[it] }
                    cachedProducts = products
                    onResult(products, if (products.isEmpty()) BillingClient.BillingResponseCode.ITEM_UNAVAILABLE else null)
                }
            }
        }
    }

    // ------------------------------------------------------------------ Compra

    fun launchPurchase(activity: Activity, product: SupportProduct, onOutcome: (PurchaseOutcome) -> Unit) {
        val details = product.details ?: return onOutcome(PurchaseOutcome.Error(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE))

        connect { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                onOutcome(PurchaseOutcome.Error(result.responseCode))
                return@connect
            }
            val billing = client ?: return@connect onOutcome(PurchaseOutcome.Error(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED))
            if (activity.isFinishing || activity.isDestroyed) return@connect

            val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .apply { product.offerToken?.let { setOfferToken(it) } }
                .build()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productParams))
                .build()

            purchaseCallback = onOutcome
            val launch = billing.launchBillingFlow(activity, flowParams)
            if (launch.responseCode != BillingClient.BillingResponseCode.OK) {
                purchaseCallback = null
                deliverLaunchError(launch.responseCode, onOutcome)
            }
        }
    }

    /** Solta a referência da tela (onDestroy). */
    fun clearPurchaseCallback(callback: (PurchaseOutcome) -> Unit) {
        if (purchaseCallback === callback) purchaseCallback = null
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        main.post {
            val callback = purchaseCallback
            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    val outcome = handlePurchases(purchases.orEmpty(), authoritative = false)
                    // Compras que chegam fora do fluxo (ex.: Pix confirmado depois) só atualizam o estado.
                    if (outcome != null) {
                        purchaseCallback = null
                        callback?.invoke(outcome)
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    purchaseCallback = null
                    callback?.invoke(PurchaseOutcome.Canceled)
                }
                else -> {
                    purchaseCallback = null
                    if (callback != null) deliverLaunchError(result.responseCode, callback)
                }
            }
        }
    }

    private fun deliverLaunchError(code: Int, onOutcome: (PurchaseOutcome) -> Unit) {
        if (code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            queryPurchases { ok ->
                onOutcome(
                    if (ok && SupportStore.isSupporter(appContext)) PurchaseOutcome.AlreadyOwned
                    else PurchaseOutcome.Error(code)
                )
            }
            return
        }
        Timber.w("Billing flow: %s", code)
        onOutcome(PurchaseOutcome.Error(code))
    }

    private fun okResult() = BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build()
    private fun errorResult(code: Int) = BillingResult.newBuilder().setResponseCode(code).build()

    private const val MIN_REFRESH_INTERVAL_MS = 15_000L
}
