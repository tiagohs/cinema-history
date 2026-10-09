package com.tiagohs.cinema_history.support

import com.android.billingclient.api.ProductDetails

/**
 * Os três valores do apoio (mesmo benefício, "pague o quanto quiser"). Os IDs são os produtos de
 * compra única (não consumíveis) cadastrados no Play Console — ver android/docs/APOIO.md.
 * Os preços NUNCA ficam no código: vêm sempre do Play ([SupportProduct.formattedPrice]).
 */
object SupportProducts {
    const val APOIO = "apoio_vitalicio"
    const val APOIO_PLUS = "apoio_vitalicio_plus"
    const val FA = "apoio_vitalicio_fa"

    /** Ordem de exibição. O primeiro é o pré-selecionado. */
    val ALL = listOf(APOIO, APOIO_PLUS, FA)
    const val DEFAULT = APOIO
}

/**
 * Um valor pronto para a tela: preço formatado pelo Play (na moeda e no formato da conta do usuário).
 *
 * @property fullFormattedPrice preço "cheio" quando há oferta de desconto ativa (ex.: preço de
 *   lançamento), para mostrar riscado; null sem desconto.
 * @property offerToken oferta escolhida (a mais barata disponível para o usuário).
 * @property details null só no modo de prévia do debug (sem Play).
 */
data class SupportProduct(
    val id: String,
    val formattedPrice: String,
    val priceMicros: Long,
    val fullFormattedPrice: String?,
    val offerToken: String?,
    val details: ProductDetails?
) {
    val isDebugPreview: Boolean get() = details == null

    companion object {

        fun from(details: ProductDetails): SupportProduct? {
            val offers = details.oneTimePurchaseOfferDetailsList?.takeIf { it.isNotEmpty() }
                ?: listOfNotNull(details.oneTimePurchaseOfferDetails)
            val offer = offers.minByOrNull { it.priceAmountMicros } ?: return null

            // Preço cheio: a oferta base (sem offerId) se for mais cara, ou o fullPriceMicros do desconto.
            val base = offers.firstOrNull { it.offerId == null && it.priceAmountMicros > offer.priceAmountMicros }
            val fullPrice = base?.formattedPrice
                ?: offer.fullPriceMicros?.takeIf { it > offer.priceAmountMicros }?.let {
                    formatMicros(it, offer.priceCurrencyCode)
                }

            return SupportProduct(
                id = details.productId,
                formattedPrice = offer.formattedPrice,
                priceMicros = offer.priceAmountMicros,
                fullFormattedPrice = fullPrice,
                offerToken = offer.offerToken?.takeIf { it.isNotBlank() },
                details = details
            )
        }

        private fun formatMicros(micros: Long, currencyCode: String): String? = runCatching {
            val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR"))
            format.currency = java.util.Currency.getInstance(currencyCode)
            format.format(micros / 1_000_000.0)
        }.getOrNull()

        /**
         * Só para builds de DEBUG com a oferta forçada e sem acesso aos produtos do Play (app
         * instalado fora da Play Store): permite ver e testar a tela. A "compra" é simulada.
         */
        fun debugPreview(): List<SupportProduct> = listOf(
            SupportProduct(SupportProducts.APOIO, "R$ 14,90 (debug)", 14_900_000, "R$ 19,90", null, null),
            SupportProduct(SupportProducts.APOIO_PLUS, "R$ 29,90 (debug)", 29_900_000, null, null, null),
            SupportProduct(SupportProducts.FA, "R$ 49,90 (debug)", 49_900_000, null, null, null)
        )
    }
}
