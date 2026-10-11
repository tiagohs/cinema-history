package com.tiagohs.cinemahistory.shared.regras

import com.tiagohs.cinemahistory.shared.model.Idioma

/** Quem pode ouvir a narração (AudioAccess do Android). */
enum class AcessoAoAudio {
    /** Sem oferta e não apoia: nenhum controle de áudio. */
    OCULTO,
    /** Oferta disponível e não apoia: só a Abertura (faixa 00) toca; o resto abre a tela de apoio. */
    BLOQUEADO,
    /** Apoiador: tudo liberado. */
    LIBERADO,
}

/** Regras do "Apoie o Cinema History" (UC-03, UC-16, UC-17, UC-39 a UC-42), iguais às do Android. */
object Apoio {
    /** Os três valores (mesmo benefício). No iOS são produtos não consumíveis com os mesmos IDs do Android. */
    const val APOIO = "apoio_vitalicio"
    const val APOIO_PLUS = "apoio_vitalicio_plus"
    const val FA = "apoio_vitalicio_fa"
    val PRODUTOS = listOf(APOIO, APOIO_PLUS, FA)
    const val PRODUTO_PADRAO = APOIO

    /**
     * A oferta só existe no Brasil com o app em português. [paisDaLoja] aceita o código de 2 letras (Play: "BR")
     * e o de 3 letras que o StoreKit devolve (Storefront.countryCode: "BRA").
     */
    fun ofertaDisponivel(paisDaLoja: String?, idioma: Idioma): Boolean =
        paisDaLoja?.uppercase() in setOf("BR", "BRA") && idioma == Idioma.PT

    fun acessoAoAudio(apoiador: Boolean, ofertaDisponivel: Boolean): AcessoAoAudio = when {
        apoiador -> AcessoAoAudio.LIBERADO
        ofertaDisponivel -> AcessoAoAudio.BLOQUEADO
        else -> AcessoAoAudio.OCULTO
    }

    /** A tela de apoio só abre para quem tem oferta ou já apoia (Supporter.openSupportScreen). */
    fun podeAbrirTelaDeApoio(apoiador: Boolean, ofertaDisponivel: Boolean): Boolean = apoiador || ofertaDisponivel

    /** Card de apoio no fim do capítulo e página "Quer ajudar?" do onboarding: só com oferta e para quem ainda não apoia. */
    fun mostrarConviteDeApoio(apoiador: Boolean, ofertaDisponivel: Boolean): Boolean = !apoiador && ofertaDisponivel
}
