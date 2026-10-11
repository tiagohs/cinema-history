package com.tiagohs.cinemahistory.shared

import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.regras.ChaveDoCapitulo
import com.tiagohs.cinemahistory.shared.regras.ConfigDoAudio
import com.tiagohs.cinemahistory.shared.regras.ManifestDoAudio
import com.tiagohs.cinemahistory.shared.regras.PosicaoNoAudio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Portado do AudioManifestTest do Android, com o formato real do manifest. */
class AudioTest {
    private val chave = ChaveDoCapitulo(Idioma.PT, 1, 1)
    private val pasta = "https://audio.example.invalid/cinema-history/pt/main_1/page_1/"
    private val json = """
        {"title":"Capítulo","era_title":null,"duration_s":70.0,"tracks":[
          {"id":"00","title":"Abertura","kind":"open","file":"00.ogg","duration_s":10.0,"bytes":100,
           "marks":[{"seg":"00-02","t":5.0,"source_index":-1},{"seg":"00-01","t":0.25,"source_index":-1}]},
          {"id":"01","title":"Parte 1","kind":"body","file":"01.ogg","file_m4a":"01.m4a","duration_s":30.0,"bytes":100,
           "marks":[{"seg":"01-01","t":0.25,"source_index":0},{"seg":"01-02","t":12.0,"source_index":1}]},
          {"id":"02","title":"Parte 2","kind":"body","duration_s":30,"bytes":100,
           "marks":[{"seg":"02-01","t":0.25,"source_index":3},{"seg":"02-02","t":20.0,"source_index":5}]}
        ]}
    """.trimIndent()
    private fun manifest() = ManifestDoAudio.ler(json, chave, pasta)

    @Test
    fun `bloco lido segue as marcas`() {
        val m = manifest()
        assertEquals(-1, m.blocoEm(0, 6_000)) // abertura: sem parágrafo
        assertEquals(0, m.blocoEm(1, 0))
        assertEquals(0, m.blocoEm(1, 11_000))
        assertEquals(1, m.blocoEm(1, 12_000))
        assertEquals(1, m.blocoEm(1, 11_900)) // tolerância de 150 ms do Android
        assertEquals(5, m.blocoEm(2, 29_000))
        assertEquals(-1, m.blocoEm(9, 0))
    }

    @Test
    fun `localizar acha o bloco ou o proximo narrado`() {
        val m = manifest()
        assertEquals(PosicaoNoAudio(1, 12_000), m.localizar(1))
        assertEquals(PosicaoNoAudio(2, 250), m.localizar(2)) // bloco 2 (imagem) não é narrado: começa no 3
        assertEquals(PosicaoNoAudio(2, 20_000), m.localizar(5))
        assertNull(m.localizar(9))
        assertEquals(setOf(0, 1, 3, 5), m.blocosNarrados)
    }

    @Test
    fun `faixas, marcas ordenadas, arquivo m4a e urls`() {
        val m = manifest()
        assertEquals(listOf(0.25, 5.0), m.faixas[0].marcas.map { it.segundos })
        assertTrue(m.faixas[0].livre)
        assertEquals("$pasta" + "00.m4a", m.urlDaFaixa(m.faixas[0]))
        assertEquals("$pasta" + "01.m4a", m.urlDaFaixa(m.faixas[1]))
        assertEquals("02.ogg", m.faixas[2].arquivo) // sem "file": id + .ogg, como o Android
        assertNull(m.tituloDaEra)
        assertEquals(1, m.minutos)
        assertEquals(300, m.bytesTotais)
        assertEquals("https://x.dev/audio-test/tones/b.ogg", ConfigDoAudio.resolver("https://x.dev/audio-test/pt/main_1/page_1/", "../../../tones/b.ogg"))
        assertEquals("https://cinema-history-audio.tiago-silva-93.workers.dev/pt/main_1/page_1/manifest.json", ConfigDoAudio.urlDoManifest(chave))
    }

    @Test
    fun `ids de midia e manifest sem faixas`() {
        assertEquals("pt/1/1#03", chave.idDaMidia("03"))
        assertEquals(chave, ChaveDoCapitulo.ler("pt/1/1#03"))
        assertEquals("03", ChaveDoCapitulo.faixaDaMidia("pt/1/1#03"))
        assertNull(ChaveDoCapitulo.ler("xx/1/1"))
        assertFailsWith<IllegalArgumentException> { ManifestDoAudio.ler("""{"tracks":[]}""", chave, pasta) }
    }
}
