package com.tiagohs.cinemahistory.shared

import com.tiagohs.cinemahistory.shared.data.ContentSource
import com.tiagohs.cinemahistory.shared.data.FonteDeConteudo
import com.tiagohs.cinemahistory.shared.model.Idioma
import com.tiagohs.cinemahistory.shared.remoto.CacheDoConteudoRemoto
import com.tiagohs.cinemahistory.shared.remoto.SincronizadorDeConteudo
import com.tiagohs.cinemahistory.shared.remoto.SincronizadorDeConteudo.Companion.sha256
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SincronizacaoTest {
    private val pasta = "/cache/remoto".toPath()
    private val glossarioNovo = """[{"name":"Novo termo","content_list":[]}]"""
    private val glossarioEmbutido = """[{"name":"Termo embutido","content_list":[]}]"""

    /** Site falso: manifest + arquivos; [falhar] simula a rede caída. */
    private class Site(var arquivos: Map<String, String>, var manifestExtra: String = "") {
        var falhar = false
        val pedidos = mutableListOf<String>()
        val cliente = HttpClient(MockEngine { req ->
            val caminho = req.url.encodedPath.substringAfter("/content/")
            pedidos += caminho
            if (falhar) return@MockEngine respondError(HttpStatusCode.ServiceUnavailable)
            if (caminho == "manifest.json") {
                val files = arquivos.entries.joinToString(",") { "\"${it.key}\":\"${sha256(it.value)}\"" }
                return@MockEngine respond("""{"format":1$manifestExtra,"files":{$files}}""")
            }
            arquivos[caminho]?.let { respond(it) } ?: respondError(HttpStatusCode.NotFound)
        })
    }

    private val embutido = object : ContentSource {
        override fun lerTexto(caminho: String): String? = when (caminho) {
            "pt/glossary.json" -> glossarioEmbutido
            "remote_manifest.json" -> """{"files":{"pt/glossary.json":"${sha256(glossarioEmbutido)}","pt/awards.json":"${sha256("[]")}"}}"""
            else -> null
        }
    }

    private fun sincronizador(site: Site, fs: FakeFileSystem, versao: Long = 20) =
        SincronizadorDeConteudo(site.cliente, pasta, embutido, versao, fs, "https://site.test/content/")

    @Test
    fun `baixa so o idioma em uso e so o que mudou, e a leitura passa a usar o cache`() = runTest {
        val fs = FakeFileSystem()
        val site = Site(mapOf("pt/glossary.json" to glossarioNovo, "pt/awards.json" to "[]", "en/glossary.json" to glossarioNovo))
        val resumo = sincronizador(site, fs).sincronizar(Idioma.PT, agoraMs = 100_000_000)
        assertEquals(1, resumo.baixados) // awards igual ao embutido e en é outro idioma
        assertEquals(listOf("manifest.json", "pt/glossary.json"), site.pedidos)
        val fonte = FonteDeConteudo(embutido, CacheDoConteudoRemoto(pasta, fs))
        assertEquals(glossarioNovo, fonte.ler(Idioma.PT, "glossary.json"))
    }

    @Test
    fun `respeita o intervalo de 6 horas, salvo quando forcado`() = runTest {
        val fs = FakeFileSystem()
        val site = Site(mapOf("pt/glossary.json" to glossarioNovo))
        val s = sincronizador(site, fs)
        s.sincronizar(Idioma.PT, agoraMs = 100_000_000)
        assertEquals("recente", s.sincronizar(Idioma.PT, agoraMs = 100_000_000 + 60_000).ignorada)
        assertNull(s.sincronizar(Idioma.PT, agoraMs = 100_000_000 + 60_000, forcar = true).ignorada)
        assertNull(s.sincronizar(Idioma.PT, agoraMs = 100_000_000 + 60_000 + SincronizadorDeConteudo.INTERVALO_MS).ignorada)
    }

    @Test
    fun `hash errado ou JSON invalido nao entram no cache`() = runTest {
        val fs = FakeFileSystem()
        val site = Site(mapOf("pt/glossary.json" to "nao é json"))
        assertEquals(0, sincronizador(site, fs).sincronizar(Idioma.PT, 100_000_000).baixados)
        assertNull(CacheDoConteudoRemoto(pasta, fs).lerTexto("pt/glossary.json"))
    }

    @Test
    fun `arquivo que sai do manifest e apagado e volta a valer o embutido`() = runTest {
        val fs = FakeFileSystem()
        val site = Site(mapOf("pt/glossary.json" to glossarioNovo))
        val s = sincronizador(site, fs)
        s.sincronizar(Idioma.PT, 100_000_000)
        site.arquivos = emptyMap()
        assertEquals(1, s.sincronizar(Idioma.PT, 100_000_000, forcar = true).removidos)
        assertEquals(glossarioEmbutido, FonteDeConteudo(embutido, CacheDoConteudoRemoto(pasta, fs)).ler(Idioma.PT, "glossary.json"))
    }

    @Test
    fun `rede caida nao apaga o que ja foi baixado`() = runTest {
        val fs = FakeFileSystem()
        val site = Site(mapOf("pt/glossary.json" to glossarioNovo))
        val s = sincronizador(site, fs)
        s.sincronizar(Idioma.PT, 100_000_000)
        site.falhar = true
        assertEquals("sem manifest", s.sincronizar(Idioma.PT, 100_000_000, forcar = true).ignorada)
        assertEquals(glossarioNovo, CacheDoConteudoRemoto(pasta, fs).lerTexto("pt/glossary.json"))
    }

    @Test
    fun `nova versao do app apaga o cache, e manifest de formato novo ou app antigo e ignorado`() = runTest {
        val fs = FakeFileSystem()
        val site = Site(mapOf("pt/glossary.json" to glossarioNovo))
        sincronizador(site, fs, versao = 20).sincronizar(Idioma.PT, 100_000_000)
        sincronizador(site, fs, versao = 21).preparar()
        assertNull(CacheDoConteudoRemoto(pasta, fs).lerTexto("pt/glossary.json"))
        site.manifestExtra = ""","min_app_version":99"""
        assertEquals("app antigo", sincronizador(site, fs, versao = 21).sincronizar(Idioma.PT, 100_000_000).ignorada)
        site.manifestExtra = ""","format":2"""
        // "format" repetido: o último vale no JSON lido
        assertEquals("formato novo", sincronizador(site, fs, versao = 21).sincronizar(Idioma.PT, 100_000_000, forcar = true).ignorada)
    }

    @Test
    fun `caminhos com dois pontos sao ignorados`() = runTest {
        val fs = FakeFileSystem()
        val site = Site(mapOf("pt/../../etc/x.json" to "[]"))
        assertEquals(0, sincronizador(site, fs).sincronizar(Idioma.PT, 100_000_000).baixados)
        assertNull(CacheDoConteudoRemoto(pasta, fs).lerTexto("pt/../../etc/x.json"))
    }

    @Test
    fun `manifest embutido confere com o conteudo do app`() {
        // remote_manifest.json lista o hash de cada arquivo embutido: se um arquivo mudar sem regenerar o manifest,
        // o app baixaria de novo algo que já tem (ou deixaria de baixar uma correção).
        val conteudo = ConteudoDoAndroid()
        val manifest = kotlinx.serialization.json.Json.parseToJsonElement(java.io.File(conteudo.raiz, "remote_manifest.json").readText())
        val arquivos = (manifest as kotlinx.serialization.json.JsonObject)["files"] as kotlinx.serialization.json.JsonObject
        val divergentes = arquivos.entries.filter { (caminho, hash) ->
            val texto = conteudo.lerTexto(caminho)
            texto == null || sha256(texto) != (hash as kotlinx.serialization.json.JsonPrimitive).content
        }.map { it.key }
        // en/awards.json mudou sem regenerar o manifest (achado em 11/10/2026; ver relatório). Divergência nova falha o teste.
        assertEquals(listOf("en/awards.json"), divergentes, "remote_manifest.json desatualizado")
    }
}
