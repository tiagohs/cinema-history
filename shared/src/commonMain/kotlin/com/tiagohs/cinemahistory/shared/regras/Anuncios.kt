package com.tiagohs.cinemahistory.shared.regras

/**
 * Limites dos anúncios (AdsConfig do Android): padrões usados offline; o Remote Config pode trocar.
 */
data class ConfigDeAnuncios(
    val nativoAtivo: Boolean = true,
    val intersticialAtivo: Boolean = true,
    val intervaloMinimoMs: Long = 240_000,
    val limiteDiario: Long = 6,
    val pularPrimeirosCapitulos: Long = 2,
)

/**
 * Regras dos anúncios (UC-15, UC-46), iguais às do Android (NativeAdAdapter, ChapterInterstitial, AdsHistory):
 * - nada para quem apoia, nada sem consentimento para pedir anúncios;
 * - intersticial só ao tocar em "próximo capítulo", nunca na primeira sessão, nunca nos primeiros toques da leitura,
 *   no máximo 1 a cada [ConfigDeAnuncios.intervaloMinimoMs] e [ConfigDeAnuncios.limiteDiario] por dia.
 */
class PoliticaDeAnuncios(
    private val preferencias: Preferencias,
    private val relogio: Relogio,
    var config: ConfigDeAnuncios = ConfigDeAnuncios(),
) {
    private var pedidosDeProximo = 0L

    /** Chamar uma vez por abertura do app. */
    fun registrarSessao() = preferencias.gravarLongo(SESSOES, sessoes + 1)

    val sessoes: Long get() = preferencias.longo(SESSOES, 0)

    fun podeMostrarNativo(apoiador: Boolean, podePedirAnuncios: Boolean): Boolean =
        !apoiador && podePedirAnuncios && config.nativoAtivo

    /** Vale a pena carregar um intersticial (pré-carga). */
    fun intersticialPossivel(apoiador: Boolean, podePedirAnuncios: Boolean): Boolean =
        !apoiador && config.intersticialAtivo && podePedirAnuncios && sessoes > 1 && intersticiaisHoje() < config.limiteDiario

    /** Chamar a cada toque em "próximo capítulo": diz se o intersticial carregado deve aparecer agora. */
    fun aoPedirProximoCapitulo(apoiador: Boolean, podePedirAnuncios: Boolean): Boolean {
        pedidosDeProximo++
        return intersticialPossivel(apoiador, podePedirAnuncios) &&
            pedidosDeProximo > config.pularPrimeirosCapitulos &&
            relogio.agoraMs() - preferencias.longo(ULTIMO, 0) >= config.intervaloMinimoMs
    }

    /** Chamar quando o intersticial de fato aparecer. */
    fun registrarIntersticial() {
        val contagem = intersticiaisHoje() + 1 // antes de trocar o dia guardado
        preferencias.gravarLongo(ULTIMO, relogio.agoraMs())
        preferencias.gravarLongo(DIA, relogio.diaLocal().toLong())
        preferencias.gravarLongo(CONTAGEM_DO_DIA, contagem)
    }

    fun intersticiaisHoje(): Long =
        if (preferencias.longo(DIA, 0) == relogio.diaLocal().toLong()) preferencias.longo(CONTAGEM_DO_DIA, 0) else 0

    private companion object {
        const val SESSOES = "anuncios.sessoes"
        const val ULTIMO = "anuncios.ultimo_intersticial"
        const val DIA = "anuncios.dia"
        const val CONTAGEM_DO_DIA = "anuncios.contagem_do_dia"
    }
}
