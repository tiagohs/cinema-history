@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package com.tiagohs.cinemahistory.shared.spike

import com.tiagohs.cinemahistory.shared.model.Resultado
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.incrementAndFetch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Resultado concreto (sem genéricos nem sealed): o formato recomendado para a fachada exposta ao Swift. */
class ListaResultado(val itens: List<String>?, val erro: String?)

/** Estado do player (UC-18) e progresso de download (UC-21), observados pelo Swift. */
class PlayerEstado(val posicaoMs: Long, val tocando: Boolean, val sequencia: Int)

class Tipos(
    val inteiro: Int,
    val longo: Long,
    val decimal: Double,
    val opcional: Int?,
    val lista: List<Int>,
    val mapa: Map<String, Int>,
    val booleano: Boolean,
)

/** Porta implementada em Swift (StoreKit 2): a compra é nativa, a regra fica no núcleo (UC-40). */
interface LojaDeApoio {
    @Throws(Exception::class)
    suspend fun comprar(produto: String): Boolean
}

class ServicoDeApoio(private val loja: LojaDeApoio) {
    @Throws(Exception::class)
    suspend fun apoiar(produto: String): String = if (loja.comprar(produto)) "apoiador" else "sem-apoio"
}

/** Ganchos de coleta de lixo para os testes de memória (S9). */
expect object Memoria {
    fun coletar()
}

/**
 * Protótipo da ponte Kotlin↔Swift sem plugin. Cada função cobre uma situação crítica do app;
 * os testes em Swift (CinemaHistoryTests/PonteSpikeTests.swift) exercitam e medem cada uma.
 */
class PonteSpike {
    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // S1 · resultado genérico x concreto
    fun resultadoGenerico(ok: Boolean): Resultado<List<String>> =
        if (ok) Resultado.Sucesso(listOf("a", "b")) else Resultado.Erro("falhou")

    fun resultadoConcreto(ok: Boolean): ListaResultado =
        if (ok) ListaResultado(listOf("a", "b"), null) else ListaResultado(null, "falhou")

    // S2 · os 16 tipos de bloco
    fun blocos(): List<Bloco> = blocosDeExemplo()
    fun todosOsTipos(): List<BlocoTipo> = BlocoTipo.entries

    // S3 · suspend simples
    @Throws(Exception::class)
    suspend fun somar(a: Int, b: Int): Int = withContext(Dispatchers.Default) { delay(20); a + b }

    // S4 · cancelamento
    private val passos = AtomicInt(0)
    private val cancelada = AtomicBoolean(false)
    val passosDaTarefaLonga: Int get() = passos.load()
    val tarefaLongaFoiCancelada: Boolean get() = cancelada.load()

    @Throws(Exception::class)
    suspend fun tarefaLonga(duracaoMs: Long): Int {
        passos.store(0); cancelada.store(false)
        try {
            val fim = duracaoMs / 10
            for (i in 0 until fim) {
                delay(10)
                passos.incrementAndFetch()
            }
            return passos.load()
        } catch (e: kotlinx.coroutines.CancellationException) {
            cancelada.store(true)
            throw e
        }
    }

    fun tarefaLongaCancelavel(duracaoMs: Long, aoTerminar: (Int) -> Unit): Cancelavel {
        passos.store(0); cancelada.store(false)
        val job = escopo.launch {
            try {
                for (i in 0 until duracaoMs / 10) {
                    delay(10)
                    passos.incrementAndFetch()
                }
                aoTerminar(passos.load())
            } catch (e: kotlinx.coroutines.CancellationException) {
                cancelada.store(true)
                throw e
            }
        }
        return Cancelavel(job)
    }

    // S5 · erros
    @Throws(Exception::class)
    suspend fun falharComErro(): String {
        delay(5)
        throw IllegalStateException("Sem internet")
    }

    @Throws(Exception::class)
    fun falharSincrono(): String = throw IllegalArgumentException("JSON inválido")

    // S6 · fluxo de estado (player, download, idioma, apoio)
    private val estado = MutableStateFlow(PlayerEstado(0, false, 0))
    val estadoAtual: StateFlow<PlayerEstado> = estado.asStateFlow()

    fun observarPlayer(aoMudar: (PlayerEstado) -> Unit): Cancelavel {
        val job = escopo.launch { estado.collect { aoMudar(it) } }
        return Cancelavel(job)
    }

    fun emitir(quantos: Int, intervaloMs: Long) {
        escopo.launch {
            for (i in 1..quantos) {
                estado.value = PlayerEstado(i * 250L, true, i)
                if (intervaloMs > 0) delay(intervaloMs)
            }
        }
    }

    // S7 · chamadas de alta frequência (destaque da narração, posição do áudio)
    private val marcas = LongArray(2000) { it * 1500L }
    fun trechoAtual(posicaoMs: Long): Int {
        var lo = 0; var hi = marcas.size - 1
        while (lo < hi) {
            val mid = (lo + hi + 1) / 2
            if (marcas[mid] <= posicaoMs) lo = mid else hi = mid - 1
        }
        return lo
    }

    private val posicoes = AtomicInt(0)
    fun salvarPosicao(capitulo: String, posicaoMs: Long) { posicoes.incrementAndFetch() }
    val posicoesSalvas: Int get() = posicoes.load()

    // S8 · callback vindo de outra thread
    fun chamarDeVolta(aoChamar: () -> Unit) {
        escopo.launch { aoChamar() }
    }

    // S9 · memória: observadores guardados pelo núcleo
    private val observadores = mutableListOf<() -> Unit>()
    fun registrar(aoNotificar: () -> Unit): Int { observadores += aoNotificar; return observadores.size }
    fun limparObservadores() { observadores.clear() }
    fun coletarLixo() = Memoria.coletar()

    // S11 · desempenho com o conteúdo real
    fun contarBlocos(textoDoCapitulo: String): Int {
        val raiz = Json.parseToJsonElement(textoDoCapitulo)
        val lista: JsonArray = (raiz as? JsonObject)?.get("content_list")?.jsonArray ?: return -1
        return lista.count { it.jsonObject["type"]?.jsonPrimitive?.content != null }
    }

    // S12 · tipos
    fun tipos(): Tipos = Tipos(7, 9_000_000_000L, 1.5, null, listOf(1, 2, 3), mapOf("a" to 1), true)
    fun ecoarOpcional(valor: Int?): Int? = valor?.plus(1)
}
