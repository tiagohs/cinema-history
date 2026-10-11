package com.tiagohs.cinemahistory.shared

import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.regras.DestinoDoLink
import com.tiagohs.cinemahistory.shared.regras.Links
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LinksTest {
    @Test
    fun `formatos do conteudo`() {
        assertEquals(11523L, assertIs<DestinoDoLink.Pessoa>(Links.destino("https://_{'type': 'screen', 'id': 11523, 'screen_type': 'person'}")).id)
        assertEquals(124043L, assertIs<DestinoDoLink.Filme>(Links.destino("https://_{type: 'screen', 'id': 124043, 'screen_type': movie}")).id)
        assertEquals(7L, assertIs<DestinoDoLink.Filme>(Links.destino("https://_{type: 'screen', 'id': 7 , 'screen_type': movie}")).id)
        assertEquals("https://www.themoviedb.org/terms-of-use",
            assertIs<DestinoDoLink.Online>(Links.destino("https://_{'type': 'online', 'id': 11523, 'url': 'https://www.themoviedb.org/terms-of-use'}")).url)
        assertEquals("https://example.com", assertIs<DestinoDoLink.Online>(Links.destino("https://example.com")).url)
    }

    @Test
    fun `link quebrado vira invalido em vez de excecao`() {
        assertIs<DestinoDoLink.Invalido>(Links.destino("https://_{type: 'screen', 'id': , 'screen_type': movie}"))
        assertIs<DestinoDoLink.Invalido>(Links.destino("https://_{type: 'screen', 'id': , 20649 'screen_type': movie}"))
        assertIs<DestinoDoLink.Invalido>(Links.destino("https://_{nada}"))
    }

    /**
     * Varre todos os links internos do conteúdo real. Os únicos quebrados aceitos são os já conhecidos
     * (id vazio em 3 arquivos e uma url vazia nas referências, por idioma); um link quebrado novo faz o teste falhar.
     */
    @Test
    fun `todos os links do conteudo real tem destino`() {
        val raiz = ConteudoDoAndroid().raiz
        val conhecidos = setOf("pages/main_6/main_6_page_2.json", "pages/main_6/main_6_page_5.json", "timelines/timeline_3.json", "references.json")
        var total = 0
        val quebrados = mutableSetOf<String>()
        for (idioma in Idioma.entries) {
            val pasta = File(raiz, idioma.pasta)
            pasta.walkTopDown().filter { it.extension == "json" }.forEach { arquivo ->
                // no JSON as aspas do href vêm escapadas (\")
                val texto = arquivo.readText().replace("\\\"", "\"")
                for (link in Links.linksDoHtml(texto)) {
                    total++
                    if (Links.destino(link) is DestinoDoLink.Invalido) quebrados += arquivo.relativeTo(pasta).path
                }
            }
        }
        assertTrue(total > 5000, "esperava milhares de links, achou $total")
        assertEquals(conhecidos, quebrados, "links quebrados no conteúdo")
    }
}
