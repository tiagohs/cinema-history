package com.tiagohs.cinemahistory.shared.remoto

import com.tiagohs.cinemahistory.shared.data.ContentSource
import com.tiagohs.cinemahistory.shared.data.lerJson
import com.tiagohs.cinemahistory.shared.model.Idioma
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import okio.ByteString.Companion.encodeUtf8
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/** Sistema de arquivos do aparelho (FileSystem.SYSTEM do okio em cada plataforma). */
expect val sistemaDeArquivos: FileSystem

/** Cliente HTTP da plataforma (Darwin no iOS, OkHttp na JVM/Android). */
expect fun criarClienteHttp(): HttpClient

/** Lê o conteúdo baixado do site, guardado em [pasta] com o mesmo caminho do manifest ("pt/glossary.json"). */
class CacheDoConteudoRemoto(private val pasta: Path, private val arquivos: FileSystem = sistemaDeArquivos) : ContentSource {
    constructor(pasta: String) : this(pasta.toPath())

    override fun lerTexto(caminho: String): String? {
        if (caminho.contains("..")) return null
        val alvo = pasta / caminho
        return try {
            if (arquivos.metadataOrNull(alvo)?.isRegularFile == true) arquivos.read(alvo) { readUtf8() } else null
        } catch (e: Exception) {
            null
        }
    }
}

/** O que a sincronização fez (para log e testes). */
data class ResumoDaSincronizacao(val baixados: Int, val removidos: Int, val ignorada: String?)

/**
 * Conteúdo atualizável sem nova versão do app (UC-33, UC-44): a mesma lógica do RemoteContent do Android.
 *
 * - manifest em [urlBase]manifest.json lista os arquivos publicados com o sha256 de cada um;
 * - só baixa o idioma em uso, só o que difere do conteúdo embutido e do que já está no cache;
 * - confere hash e JSON antes de gravar, grava de forma atômica e remove o que saiu do manifest;
 * - no máximo a cada 6 h (salvo [sincronizar] com forcar), e o cache é apagado quando o app muda de versão;
 * - falha de rede nunca apaga nada: fica o que já havia.
 */
class SincronizadorDeConteudo(
    private val http: HttpClient,
    private val pasta: Path,
    private val embutido: ContentSource,
    private val versaoDoApp: Long,
    private val arquivos: FileSystem = sistemaDeArquivos,
    private val urlBase: String = URL_BASE,
) {
    private val estado = pasta / ARQUIVO_DE_ESTADO

    /**
     * Chamar ao abrir o app: sem rede; apaga o cache se o app mudou de versão.
     * Nunca lança (regra 4 da ponte): falha de disco só deixa o cache como está.
     */
    fun preparar() {
        try {
            val dados = lerEstado()
            if (dados.versaoDoApp != versaoDoApp) {
                arquivos.deleteRecursively(pasta, mustExist = false)
                arquivos.createDirectories(pasta)
                gravarEstado(Estado(versaoDoApp, 0, emptyMap()))
            }
        } catch (e: Exception) {
            // disco cheio ou sem permissão: segue com o conteúdo embutido
        }
    }

    suspend fun sincronizar(idioma: Idioma, agoraMs: Long, forcar: Boolean = false): ResumoDaSincronizacao {
        preparar()
        val dados = lerEstado()
        if (!forcar && agoraMs - dados.ultimaSincronizacao < INTERVALO_MS) return ResumoDaSincronizacao(0, 0, "recente")
        val manifest = baixar(urlBase + "manifest.json")?.let { runCatching { lerJson(it) as JsonObject }.getOrNull() }
            ?: return ResumoDaSincronizacao(0, 0, "sem manifest")
        if (manifest.numero("format", 1) > FORMATO) return ResumoDaSincronizacao(0, 0, "formato novo")
        if (manifest.numero("min_app_version", 0) > versaoDoApp) return ResumoDaSincronizacao(0, 0, "app antigo")
        val publicados = (manifest["files"] as? JsonObject) ?: return ResumoDaSincronizacao(0, 0, "manifest sem arquivos")
        val embutidos = hashesEmbutidos()
        val hashes = dados.hashes.toMutableMap()
        val manter = mutableSetOf<String>()
        var baixados = 0
        for ((caminho, valor) in publicados) {
            val hash = (valor as? JsonPrimitive)?.content ?: continue
            if (caminho.contains("..") || !caminho.startsWith("${idioma.pasta}/")) continue
            if (embutidos[caminho] == hash) continue
            manter += caminho
            val alvo = pasta / caminho
            if (hashes[caminho] == hash && arquivos.exists(alvo)) continue
            val corpo = baixar(urlBase + caminho) ?: continue
            if (sha256(corpo) != hash || !ehJson(corpo)) continue
            alvo.parent?.let { arquivos.createDirectories(it) }
            val temporario = "$alvo.tmp".toPath()
            arquivos.write(temporario) { writeUtf8(corpo) }
            arquivos.atomicMove(temporario, alvo)
            hashes[caminho] = hash
            baixados++
        }
        var removidos = 0
        arquivos.listRecursively(pasta).toList().forEach { arquivo ->
            if (arquivo == estado || arquivos.metadataOrNull(arquivo)?.isRegularFile != true) return@forEach
            val relativo = arquivo.relativeTo(pasta).toString().replace('\\', '/')
            if (relativo !in manter) {
                arquivos.delete(arquivo)
                hashes.remove(relativo)
                removidos++
            }
        }
        gravarEstado(Estado(versaoDoApp, agoraMs, hashes))
        return ResumoDaSincronizacao(baixados, removidos, null)
    }

    private suspend fun baixar(url: String): String? = try {
        val resposta = http.get(url)
        if (resposta.status.isSuccess()) resposta.bodyAsText() else null
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        null
    }

    /** Hashes do conteúdo que já veio no app (local/remote_manifest.json, gerado por content-src/remote.py). */
    private fun hashesEmbutidos(): Map<String, String> = runCatching {
        val arquivosDoManifest = (lerJson(embutido.lerTexto("remote_manifest.json")!!) as JsonObject)["files"] as JsonObject
        arquivosDoManifest.mapValues { (it.value as JsonPrimitive).content }
    }.getOrDefault(emptyMap())

    private data class Estado(val versaoDoApp: Long, val ultimaSincronizacao: Long, val hashes: Map<String, String>)

    private fun lerEstado(): Estado = runCatching {
        val o = lerJson(arquivos.read(estado) { readUtf8() }) as JsonObject
        Estado(
            o.numero("versao_do_app", -1),
            o.numero("ultima_sincronizacao", 0),
            (o["hashes"] as? JsonObject)?.mapValues { (it.value as JsonPrimitive).content } ?: emptyMap(),
        )
    }.getOrDefault(Estado(-1, 0, emptyMap()))

    private fun gravarEstado(e: Estado) {
        arquivos.createDirectories(pasta)
        val json = JsonObject(
            mapOf(
                "versao_do_app" to JsonPrimitive(e.versaoDoApp),
                "ultima_sincronizacao" to JsonPrimitive(e.ultimaSincronizacao),
                "hashes" to JsonObject(e.hashes.mapValues { JsonPrimitive(it.value) }),
            ),
        )
        arquivos.write(estado) { writeUtf8(json.toString()) }
    }

    companion object {
        const val URL_BASE = "https://website-cb5.pages.dev/cinema-history/content/"
        const val INTERVALO_MS = 6 * 60 * 60 * 1000L
        const val FORMATO = 1L
        private const val ARQUIVO_DE_ESTADO = ".estado.json"

        fun sha256(texto: String): String = texto.encodeUtf8().sha256().hex()

        fun ehJson(texto: String): Boolean = runCatching { lerJson(texto) }.getOrNull().let { it is JsonObject || it is JsonArray }

        private fun JsonObject.numero(chave: String, padrao: Long): Long = (this[chave] as? JsonPrimitive)?.longOrNull ?: padrao
    }
}
