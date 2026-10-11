package com.tiagohs.cinemahistory.shared

import com.tiagohs.cinemahistory.shared.data.ContentSource
import com.tiagohs.cinemahistory.shared.domain.ListarEras
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.model.Resultado
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ListarErasTest {
    private val listar = ListarEras(ConteudoDoAndroid())

    @Test
    fun `cada idioma traz as 8 eras na ordem e com a cor de acento`() {
        val cores = listOf(
            "md_red_500", "md_purple_500", "md_orange_500", "md_indigo_900",
            "md_cyan_900", "md_teal_900", "md_light_blue_900", "md_deep_orange_900",
        )
        for (idioma in Idioma.entries) {
            val eras = assertIs<Resultado.Sucesso<List<com.tiagohs.cinemahistory.shared.model.Era>>>(listar(idioma)).valor
            assertEquals((1..8).toList(), eras.map { it.id }, "ids em $idioma")
            assertEquals(cores, eras.map { it.cor }, "cores em $idioma")
            assertTrue(eras.all { it.titulo.isNotBlank() && it.descricao.isNotBlank() }, "textos em $idioma")
        }
    }

    @Test
    fun `a primeira era em portugues e De 1895 a 1929 com citacao`() {
        val primeira = assertIs<Resultado.Sucesso<List<com.tiagohs.cinemahistory.shared.model.Era>>>(listar(Idioma.PT)).valor.first()
        assertEquals("De 1895 a 1929", primeira.titulo)
        assertEquals("Parte 01", primeira.subtitulo)
        assertNotNull(primeira.citacao).also { assertTrue(it.texto.startsWith("Labor omnia vincit")) }
    }

    @Test
    fun `fonte sem o arquivo vira erro e nao excecao`() {
        val vazia = object : ContentSource { override fun lerTexto(caminho: String): String? = null }
        assertIs<Resultado.Erro>(ListarEras(vazia)(Idioma.PT))
    }

    @Test
    fun `json invalido vira erro`() {
        val quebrada = object : ContentSource { override fun lerTexto(caminho: String): String? = "{nao e json" }
        assertIs<Resultado.Erro>(ListarEras(quebrada)(Idioma.EN))
    }

    @Test
    fun `idioma do aparelho segue pt e es e o resto cai em ingles`() {
        assertEquals(Idioma.PT, Idioma.doAparelho("pt-BR"))
        assertEquals(Idioma.ES, Idioma.doAparelho("es_419"))
        assertEquals(Idioma.EN, Idioma.doAparelho("fr-FR"))
        assertEquals(Idioma.EN, Idioma.doAparelho("en"))
    }
}
