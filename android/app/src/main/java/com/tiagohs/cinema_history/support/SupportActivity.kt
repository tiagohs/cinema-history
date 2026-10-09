package com.tiagohs.cinema_history.support

import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.android.billingclient.api.BillingClient
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.ActivitySupportBinding
import com.tiagohs.cinema_history.databinding.ItemSupportBenefitBinding
import com.tiagohs.cinema_history.databinding.ItemSupportPriceBinding
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.Motion

/**
 * "Apoie o Cinema History": pagamento único, para sempre, em 3 valores com o mesmo benefício.
 * Os preços vêm sempre do Google Play (ProductDetails). Ver [SupportBilling] e android/docs/APOIO.md.
 */
class SupportActivity : BaseActivity<ActivitySupportBinding>() {

    override fun inflateBinding(inflater: LayoutInflater) = ActivitySupportBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    private var products: List<SupportProduct> = emptyList()
    private var selectedId: String = SupportProducts.DEFAULT
    private var purchasing = false
    private var loadRequested = false

    private val supporterListener: () -> Unit = { render() }
    private val purchaseCallback: (PurchaseOutcome) -> Unit = { onPurchaseOutcome(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedId = savedInstanceState?.getString(STATE_SELECTED) ?: SupportProducts.DEFAULT

        setupInsets()
        setupBenefits()
        // Tablets: os botões fixos não esticam a tela toda (a barra continua de ponta a ponta).
        binding.bottomBar.limitContentWidth(R.dimen.ls_form_max_width)

        binding.closeButton.setOnClickListener { finish() }
        binding.continueFreeButton.setOnClickListener { finish() }
        binding.supportButton.setOnClickListener { purchase() }
        binding.restoreButton.setOnClickListener { restore() }
        binding.errorRetryButton.setOnClickListener { loadProducts() }

        Supporter.addListener(supporterListener)

        render()
        if (savedInstanceState == null) animateEntrance()
    }

    override fun onResume() {
        super.onResume()
        // Ao voltar (ex.: depois de pagar um Pix fora do app), reconfirma no Play.
        SupportBilling.refresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SELECTED, selectedId)
    }

    override fun onDestroy() {
        Supporter.removeListener(supporterListener)
        SupportBilling.clearPurchaseCallback(purchaseCallback)
        super.onDestroy()
    }

    // ------------------------------------------------------------------ Layout

    /** Edge-to-edge: o botão de fechar desce o tamanho da status bar; o conteúdo rola por baixo da barra de ações. */
    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()).top
            view.updatePadding(top = top)
            insets
        }
        binding.bottomBar.addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
            val height = bottom - top
            if (binding.supportScroll.paddingBottom != height) binding.supportScroll.updatePadding(bottom = height)
        }
    }

    private fun setupBenefits() {
        bindBenefit(binding.benefitAudio, R.drawable.ic_support_headphones_24, R.string.support_benefit_audio_title, R.string.support_benefit_audio_subtitle)
        bindBenefit(binding.benefitAds, R.drawable.ic_support_no_ads_24, R.string.support_benefit_ads_title, R.string.support_benefit_ads_subtitle)
        bindBenefit(binding.benefitPeople, R.drawable.ic_support_edit_24, R.string.support_benefit_people_title, R.string.support_benefit_people_subtitle)
    }

    private fun bindBenefit(row: ItemSupportBenefitBinding, icon: Int, title: Int, subtitle: Int) {
        row.benefitIcon.setImageResource(icon)
        row.benefitTitle.setText(title)
        row.benefitSubtitle.setText(subtitle)
    }

    private fun animateEntrance() {
        if (!Motion.enabled(this)) return
        val views = listOf(binding.supportKicker, binding.supportTitle, binding.supportIntro, binding.benefitsContainer, binding.offerSection)
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 24f * resources.displayMetrics.density
            view.animate().alpha(1f).translationY(0f)
                .setStartDelay(80L + index * 60L)
                .setDuration(320L)
                .start()
        }
    }

    // ------------------------------------------------------------------ Estado

    /** Desenha a tela conforme o estado atual (apoiador, pendente, valores). */
    private fun render() {
        val supporter = Supporter.isSupporter(this)

        binding.thanksSection.isVisible = supporter
        binding.supportTitle.isVisible = !supporter
        binding.supportIntro.isVisible = !supporter
        binding.offerSection.isVisible = !supporter
        binding.supportButton.isVisible = !supporter
        binding.restoreButton.isVisible = !supporter
        binding.continueFreeButton.setText(if (supporter) R.string.support_thanks_back else R.string.support_continue_free)

        if (supporter) return

        binding.pendingCard.isVisible = Supporter.isPurchasePending(this)
        updateSupportButton()

        // Deixou de ser apoiador com a tela aberta (ex.: reembolso): carrega os valores.
        if (!loadRequested) loadProducts()
    }

    private fun loadProducts() {
        loadRequested = true
        val cached = SupportBilling.cachedProducts
        if (cached.isNotEmpty()) {
            showProducts(cached)
        } else {
            showLoading()
        }

        SupportBilling.queryProducts { result, errorCode ->
            if (isFinishing || isDestroyed) return@queryProducts
            when {
                result.isNotEmpty() -> showProducts(result)
                SupportStore.debugForceOffer(this) -> showProducts(SupportProduct.debugPreview())
                products.isNotEmpty() -> Unit // mantém os valores já exibidos
                else -> showError(errorCode)
            }
        }
    }

    private fun showLoading() {
        binding.loadingState.isVisible = true
        binding.errorState.isVisible = false
        binding.productsContainer.isVisible = false
        updateSupportButton()
    }

    private fun showError(errorCode: Int?) {
        binding.loadingState.isVisible = false
        binding.productsContainer.isVisible = false
        binding.errorState.isVisible = true
        binding.errorText.setText(
            when (errorCode) {
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> R.string.support_error_unavailable
                else -> R.string.support_error
            }
        )
        products = emptyList()
        updateSupportButton()
    }

    private fun showProducts(list: List<SupportProduct>) {
        products = list
        if (list.none { it.id == selectedId }) selectedId = list.first().id

        binding.loadingState.isVisible = false
        binding.errorState.isVisible = false
        binding.productsContainer.isVisible = true
        binding.debugPreviewText.isVisible = list.any { it.isDebugPreview }

        val container = binding.productsContainer
        container.removeAllViews()
        list.forEach { product ->
            val card = ItemSupportPriceBinding.inflate(layoutInflater, container, false)
            card.priceName.setText(tierName(product.id))
            card.priceDescription.setText(tierDescription(product.id))
            card.priceValue.text = product.formattedPrice
            card.priceFull.isVisible = product.fullFormattedPrice != null
            card.priceFull.text = product.fullFormattedPrice
            card.priceFull.paintFlags = card.priceFull.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            card.priceBadge.isVisible = product.fullFormattedPrice != null

            val selected = product.id == selectedId
            card.root.isChecked = selected
            card.priceRadio.isChecked = selected
            card.root.contentDescription = listOfNotNull(
                getString(tierName(product.id)), getString(tierDescription(product.id)), product.formattedPrice
            ).joinToString(", ")
            card.root.setOnClickListener { select(product.id) }
            card.root.tag = product.id
            container.addView(card.root)
        }
        updateSupportButton()
    }

    private fun select(productId: String) {
        selectedId = productId
        for (i in 0 until binding.productsContainer.childCount) {
            val child = binding.productsContainer.getChildAt(i)
            val card = ItemSupportPriceBinding.bind(child)
            val selected = child.tag == productId
            card.root.isChecked = selected
            card.priceRadio.isChecked = selected
        }
        updateSupportButton()
    }

    private fun updateSupportButton() {
        val product = products.firstOrNull { it.id == selectedId }
        val enabled = product != null && !purchasing
        binding.supportButton.isEnabled = enabled
        binding.supportButton.alpha = if (enabled) 1f else 0.5f
        binding.supportButton.text = product?.let { getString(R.string.support_button, it.formattedPrice) }
            ?: getString(R.string.support_button_default)
    }

    // ------------------------------------------------------------------ Compra

    private fun purchase() {
        val product = products.firstOrNull { it.id == selectedId } ?: return

        if (product.isDebugPreview) {
            // Prévia de debug sem Play: simula a compra concluída.
            SupportStore.setDebugFlags(this, forceOffer = true, forceSupporter = true, forcePending = false)
            Supporter.notifyChanged()
            onPurchaseOutcome(PurchaseOutcome.Success)
            return
        }

        purchasing = true
        updateSupportButton()
        SupportBilling.launchPurchase(this, product, purchaseCallback)
    }

    private fun onPurchaseOutcome(outcome: PurchaseOutcome) {
        purchasing = false
        if (isFinishing || isDestroyed) return
        render()

        when (outcome) {
            PurchaseOutcome.Success -> snack(R.string.support_purchase_success)
            PurchaseOutcome.AlreadyOwned -> snack(R.string.support_restore_success)
            PurchaseOutcome.Pending -> binding.supportScroll.post {
                binding.supportScroll.smoothScrollTo(0, binding.supportScroll.getChildAt(0).height)
            }
            PurchaseOutcome.Canceled -> Unit
            is PurchaseOutcome.Error -> snack(
                if (outcome.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR ||
                    outcome.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE ||
                    outcome.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED
                ) R.string.support_error else R.string.support_purchase_error
            )
        }
    }

    private fun restore() {
        binding.restoreButton.isEnabled = false
        SupportBilling.refresh(force = true) { ok ->
            if (isFinishing || isDestroyed) return@refresh
            binding.restoreButton.isEnabled = true
            render()
            snack(
                when {
                    !ok -> R.string.support_restore_error
                    Supporter.isSupporter(this) -> R.string.support_restore_success
                    else -> R.string.support_restore_none
                }
            )
        }
    }

    private fun snack(message: Int) {
        runCatching {
            Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.bottomBar)
                .show()
        }.onFailure { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    }

    private fun tierName(id: String): Int = when (id) {
        SupportProducts.APOIO_PLUS -> R.string.support_tier_plus
        SupportProducts.FA -> R.string.support_tier_fa
        else -> R.string.support_tier_apoio
    }

    private fun tierDescription(id: String): Int = when (id) {
        SupportProducts.APOIO_PLUS -> R.string.support_tier_plus_description
        SupportProducts.FA -> R.string.support_tier_fa_description
        else -> R.string.support_tier_apoio_description
    }

    companion object {
        private const val STATE_SELECTED = "selected_product"
        const val EXTRA_SOURCE = "source"

        fun newIntent(context: Context, source: String): Intent =
            Intent(context, SupportActivity::class.java).putExtra(EXTRA_SOURCE, source)
    }
}
