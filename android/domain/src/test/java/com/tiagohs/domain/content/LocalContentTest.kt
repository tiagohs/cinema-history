package com.tiagohs.domain.content

import com.google.gson.reflect.TypeToken
import com.tiagohs.domain.services.config.LocalGson
import com.tiagohs.entities.Glossary
import com.tiagohs.entities.HomeContentItem
import com.tiagohs.entities.Page
import com.tiagohs.entities.Sumario
import com.tiagohs.entities.awards.NomineeResult
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.enums.LocalFiles
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.references.ReferenceResult
import com.tiagohs.entities.timeline.TimelineResult
import com.tiagohs.entities.tmdb.MovieExtraInfoResult
import com.tiagohs.entities.tmdb.person.PersonExtraInfo
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.lang.reflect.Type

/**
 * Garante que TODO o conteúdo de assets/local é lido sem erro pelo mesmo Gson do app,
 * em cada idioma disponível. Roda no CI: uma tradução que quebre a estrutura falha o build.
 */
class LocalContentTest {

    private val gson = LocalGson.create()

    private val assetsDir: File = listOf("../app/src/main/assets", "app/src/main/assets")
        .map { File(it) }
        .first { it.isDirectory }

    /** Pastas de idioma (local/pt, local/en...). Antes da migração, o conteúdo fica direto em local/. */
    private fun contentRoots(): List<File> {
        val local = File(assetsDir, "local")
        val langDirs = local.listFiles { f -> f.isDirectory && f.name.length == 2 }?.toList().orEmpty()
        return if (langDirs.isNotEmpty()) langDirs else listOf(local)
    }

    private fun typeFor(path: String): Type = when {
        path == "/homecontent" -> object : TypeToken<List<HomeContentItem>>() {}.type
        path in setOf("/maintopics", "/awards", "/milmoviesmaintopics", "/directorsmaintopics") ->
            object : TypeToken<List<MainTopic>>() {}.type
        path.startsWith("/awards/nominees/") -> object : TypeToken<List<NomineeResult>>() {}.type
        path.startsWith("/awards/history/") -> object : TypeToken<List<Content>>() {}.type
        path == "/references" -> object : TypeToken<List<ReferenceResult>>() {}.type
        path == "/glossary" -> object : TypeToken<List<Glossary>>() {}.type
        path.startsWith("/hmt_sumario_") -> object : TypeToken<List<Sumario>>() {}.type
        path == "/timelines" -> object : TypeToken<List<Int>>() {}.type
        path.startsWith("/timeline_") -> TimelineResult::class.java
        path == "/special_persons" -> object : TypeToken<List<PersonExtraInfo>>() {}.type
        path == "/special_movies" -> object : TypeToken<List<MovieExtraInfoResult>>() {}.type
        Regex("/main_\\d+/main_\\d+_page_\\d+").matches(path) -> Page::class.java
        else -> throw IllegalArgumentException("Sem tipo mapeado para $path (atualize LocalContentTest)")
    }

    private fun fileFor(root: File, raw: String) = File(root, raw.removePrefix("local/"))

    @Test
    fun everyLocalFileExistsInEveryLanguage() {
        val missing = contentRoots().flatMap { root ->
            LocalFiles.values().map { fileFor(root, it.raw) }.filterNot { it.isFile }.map { it.path }
        }
        assertTrue("Arquivos ausentes:\n" + missing.joinToString("\n"), missing.isEmpty())
    }

    @Test
    fun everyJsonFileIsMappedInLocalFiles() {
        contentRoots().forEach { root ->
            val mapped = LocalFiles.values().map { fileFor(root, it.raw).canonicalPath }.toSet()
            val orphan = root.walkTopDown().filter { it.isFile && it.extension == "json" }
                .filterNot { it.canonicalPath in mapped }.map { it.path }.toList()
            assertTrue("JSONs sem entrada em LocalFiles:\n" + orphan.joinToString("\n"), orphan.isEmpty())
        }
    }

    @Test
    fun everyLocalFileParsesWithAppGson() {
        val errors = mutableListOf<String>()
        contentRoots().forEach { root ->
            LocalFiles.values().forEach { entry ->
                val file = fileFor(root, entry.raw)
                if (!file.isFile) return@forEach
                try {
                    val result: Any? = gson.fromJson(file.readText(Charsets.UTF_8), typeFor(entry.path))
                    if (result == null) errors += "${file.path}: resultado nulo"
                    if (result is List<*> && result.any { it == null }) {
                        errors += "${file.path}: item nulo na lista (tipo desconhecido no deserializer?)"
                    }
                    if (result is Page && result.contentList.any { it == null }) {
                        errors += "${file.path}: bloco nulo em content_list (type desconhecido?)"
                    }
                } catch (e: Exception) {
                    errors += "${file.path}: ${e.javaClass.simpleName}: ${e.message}"
                }
            }
        }
        if (errors.isNotEmpty()) fail("Conteúdo inválido:\n" + errors.joinToString("\n"))
    }
}
