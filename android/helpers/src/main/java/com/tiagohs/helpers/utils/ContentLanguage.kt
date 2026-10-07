package com.tiagohs.helpers.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Ponto único que decide o idioma do CONTEÚDO do app (assets/local/<idioma>/) e das chamadas ao TMDB.
 *
 * Ordem de decisão:
 *  1. Idioma escolhido para o app (Configurações ou seletor de idioma por app do Android 13+);
 *  2. Idiomas do aparelho, na ordem de preferência do usuário;
 *  3. Se nenhum estiver liberado: inglês, se liberado; senão português.
 *
 * Um idioma só é usado depois de entrar em [ENABLED], ou seja, quando a tradução estiver
 * completa e revisada. Enquanto isso, o app continua 100% em português.
 */
object ContentLanguage {

    const val PORTUGUESE = "pt"
    const val ENGLISH = "en"
    const val SPANISH = "es"

    /** Idioma de origem do conteúdo: sempre existe e é o fallback final. */
    const val SOURCE = PORTUGUESE

    /** Idiomas com conteúdo traduzido, completo e revisado. Adicione ENGLISH/SPANISH quando estiverem prontos. */
    val RELEASED: List<String> = listOf(PORTUGUESE)

    /** Idiomas ativos nesta execução: [RELEASED], mais os de prévia em builds de debug (ver [enablePreview]). */
    var ENABLED: List<String> = RELEASED
        private set

    /**
     * Só para builds de DEBUG: libera idiomas ainda em tradução para revisão no aparelho.
     * Textos ainda não traduzidos aparecem em português.
     */
    fun enablePreview(languages: List<String>) {
        ENABLED = (RELEASED + languages).distinct()
    }

    /** Idioma do conteúdo a usar agora (pt, en ou es). */
    fun current(): String {
        candidates().forEach { locale ->
            val language = normalize(locale.language)
            if (language in ENABLED) return language
        }

        return if (ENGLISH in ENABLED) ENGLISH else SOURCE
    }

    /** Tag de idioma para a API do TMDB (title/overview, vídeos e imagens). */
    fun tmdbTag(language: String = current()): String = when (language) {
        PORTUGUESE -> "pt-BR"
        SPANISH -> "es-MX" // espanhol latino-americano
        else -> "en-US"
    }

    /** Pasta de assets do conteúdo: "local/x.json" -> "local/<idioma>/x.json". */
    fun assetPath(raw: String, language: String = current()): String =
        "local/$language/" + raw.removePrefix("local/")

    private fun candidates(): List<Locale> {
        val result = ArrayList<Locale>()

        val appLocales = AppCompatDelegate.getApplicationLocales()
        for (i in 0 until appLocales.size()) appLocales[i]?.let { result += it }

        val systemLocales = LocaleListCompat.getAdjustedDefault()
        for (i in 0 until systemLocales.size()) systemLocales[i]?.let { result += it }

        return result
    }

    private fun normalize(language: String): String = when (language.lowercase()) {
        "pt" -> PORTUGUESE
        "es" -> SPANISH
        "en" -> ENGLISH
        else -> language.lowercase()
    }
}
