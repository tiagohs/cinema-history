package com.tiagohs.cinemahistory.shared

import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.regras.AcessoAoAudio
import com.tiagohs.cinemahistory.shared.regras.Apoio
import com.tiagohs.cinemahistory.shared.regras.ChaveDoCapitulo
import com.tiagohs.cinemahistory.shared.regras.ConfigDeAnuncios
import com.tiagohs.cinemahistory.shared.regras.PoliticaDeAnuncios
import com.tiagohs.cinemahistory.shared.regras.PreferenciasEmMemoria
import com.tiagohs.cinemahistory.shared.regras.Progresso
import com.tiagohs.cinemahistory.shared.regras.Relogio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RelogioFalso(var agora: Long = 1_000_000L, var dia: Int = 2026_284) : Relogio {
    override fun agoraMs() = agora
    override fun diaLocal() = dia
}

class RegrasTest {

    @Test
    fun `idioma - escolha do app vence, depois os idiomas do aparelho em ordem, e o resto cai em ingles`() {
        assertEquals(Idioma.ES, Idioma.resolver("es", listOf("pt-BR")))
        assertEquals(Idioma.PT, Idioma.resolver(null, listOf("fr-FR", "pt-PT", "en-US")))
        assertEquals(Idioma.EN, Idioma.resolver(null, listOf("fr-FR", "de-DE")))
        assertEquals(Idioma.EN, Idioma.resolver(null, emptyList()))
        assertEquals(Idioma.ES, Idioma.resolver(null, listOf("es-419")))
        assertEquals("es-MX", Idioma.tagDoTmdb(Idioma.ES))
        assertEquals("pt-BR", Idioma.tagDoTmdb(Idioma.PT))
    }

    @Test
    fun `apoio - oferta so no Brasil em portugues, aceitando o codigo de 3 letras do StoreKit`() {
        assertTrue(Apoio.ofertaDisponivel("BR", Idioma.PT))
        assertTrue(Apoio.ofertaDisponivel("BRA", Idioma.PT))
        assertFalse(Apoio.ofertaDisponivel("BRA", Idioma.EN))
        assertFalse(Apoio.ofertaDisponivel("USA", Idioma.PT))
        assertFalse(Apoio.ofertaDisponivel(null, Idioma.PT))
        assertEquals(AcessoAoAudio.LIBERADO, Apoio.acessoAoAudio(apoiador = true, ofertaDisponivel = false))
        assertEquals(AcessoAoAudio.BLOQUEADO, Apoio.acessoAoAudio(apoiador = false, ofertaDisponivel = true))
        assertEquals(AcessoAoAudio.OCULTO, Apoio.acessoAoAudio(apoiador = false, ofertaDisponivel = false))
        assertTrue(Apoio.podeAbrirTelaDeApoio(apoiador = true, ofertaDisponivel = false))
        assertFalse(Apoio.mostrarConviteDeApoio(apoiador = true, ofertaDisponivel = true))
        assertEquals(listOf("apoio_vitalicio", "apoio_vitalicio_plus", "apoio_vitalicio_fa"), Apoio.PRODUTOS)
    }

    @Test
    fun `anuncios - nada para quem apoia ou sem consentimento`() {
        val p = PoliticaDeAnuncios(PreferenciasEmMemoria(), RelogioFalso())
        assertTrue(p.podeMostrarNativo(apoiador = false, podePedirAnuncios = true))
        assertFalse(p.podeMostrarNativo(apoiador = true, podePedirAnuncios = true))
        assertFalse(p.podeMostrarNativo(apoiador = false, podePedirAnuncios = false))
        p.config = ConfigDeAnuncios(nativoAtivo = false)
        assertFalse(p.podeMostrarNativo(apoiador = false, podePedirAnuncios = true))
    }

    @Test
    fun `intersticial - nunca na primeira sessao nem nos primeiros proximos, respeita intervalo e limite diario`() {
        val prefs = PreferenciasEmMemoria()
        val relogio = RelogioFalso()
        val p = PoliticaDeAnuncios(prefs, relogio)
        p.registrarSessao()
        repeat(5) { assertFalse(p.aoPedirProximoCapitulo(apoiador = false, podePedirAnuncios = true), "primeira sessão") }

        val q = PoliticaDeAnuncios(prefs, relogio)
        q.registrarSessao() // segunda sessão
        assertFalse(q.aoPedirProximoCapitulo(false, true), "1º próximo")
        assertFalse(q.aoPedirProximoCapitulo(false, true), "2º próximo")
        assertTrue(q.aoPedirProximoCapitulo(false, true), "3º próximo")
        assertFalse(q.aoPedirProximoCapitulo(true, true), "apoiador nunca")
        q.registrarIntersticial()
        assertFalse(q.aoPedirProximoCapitulo(false, true), "intervalo de 4 min")
        relogio.agora += 240_000
        assertTrue(q.aoPedirProximoCapitulo(false, true), "após 4 min")

        repeat(5) { q.registrarIntersticial() }
        relogio.agora += 10_000_000
        assertEquals(6, q.intersticiaisHoje())
        assertFalse(q.aoPedirProximoCapitulo(false, true), "limite diário de 6")
        relogio.dia += 1
        assertEquals(0, q.intersticiaisHoje())
        assertTrue(q.aoPedirProximoCapitulo(false, true), "novo dia")
        q.registrarIntersticial()
        assertEquals(1, q.intersticiaisHoje(), "contagem recomeça no dia novo")
    }

    @Test
    fun `progresso - lidos, continue lendo, posicoes e preferencias`() {
        val p = Progresso(PreferenciasEmMemoria())
        assertFalse(p.onboardingConcluido)
        assertEquals(0, p.ultimaEra)
        p.marcarLido(2, 3)
        p.marcarLido(2, 1)
        p.marcarLido(2, 3)
        assertEquals(setOf(1, 3), p.capitulosLidos(2))
        assertTrue(p.estaLido(2, 1))
        p.registrarLeitura(4, 7)
        assertEquals(4, p.ultimaEra)
        assertEquals(7, p.ultimoCapitulo(4))
        p.salvarPosicaoDeLeitura(4, 7, 1234)
        assertEquals(1234, p.posicaoDeLeitura(4, 7))
        val chave = ChaveDoCapitulo(Idioma.PT, 1, 2)
        assertNull(p.posicaoDoAudio(chave))
        p.salvarPosicaoDoAudio(chave, "02", 45_000)
        assertEquals("02", p.posicaoDoAudio(chave)?.faixa)
        assertEquals(45_000, p.posicaoDoAudio(chave)?.ms)
        p.velocidadeDoAudio = 1.5
        assertEquals(1.5, p.velocidadeDoAudio)
        p.velocidadeDoAudio = 3.0
        assertEquals(1.0, p.velocidadeDoAudio, "velocidade fora da lista volta ao normal")
        assertEquals("sistema", p.tema)
    }
}
