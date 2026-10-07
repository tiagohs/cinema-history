package com.tiagohs.helpers.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentLanguageTest {

    @Test
    fun assetPathAddsLanguageFolder() {
        assertEquals("local/pt/homecontent.json", ContentLanguage.assetPath("local/homecontent.json", "pt"))
        assertEquals(
            "local/en/pages/main_7/main_7_page_1.json",
            ContentLanguage.assetPath("local/pages/main_7/main_7_page_1.json", "en")
        )
    }

    @Test
    fun tmdbTags() {
        assertEquals("pt-BR", ContentLanguage.tmdbTag("pt"))
        assertEquals("en-US", ContentLanguage.tmdbTag("en"))
        assertEquals("es-MX", ContentLanguage.tmdbTag("es"))
    }

    @Test
    fun sourceLanguageIsAlwaysEnabled() {
        assertTrue(ContentLanguage.SOURCE in ContentLanguage.ENABLED)
    }
}
