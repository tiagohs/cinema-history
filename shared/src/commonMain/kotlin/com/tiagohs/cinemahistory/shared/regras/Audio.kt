package com.tiagohs.cinemahistory.shared.regras

import com.tiagohs.cinemahistory.shared.data.decimal
import com.tiagohs.cinemahistory.shared.data.inteiro
import com.tiagohs.cinemahistory.shared.data.lerJson
import com.tiagohs.cinemahistory.shared.data.lista
import com.tiagohs.cinemahistory.shared.data.longo
import com.tiagohs.cinemahistory.shared.data.texto
import com.tiagohs.cinemahistory.shared.model.Idioma
import kotlinx.serialization.json.JsonObject
import kotlin.math.max
import kotlin.math.roundToInt

/** Configuração da narração (AudioConfig do Android). */
object ConfigDoAudio {
    /** Worker da Cloudflare na frente do bucket R2. Termina com "/". */
    const val URL_BASE = "https://cinema-history-audio.tiago-silva-93.workers.dev/"

    /** Faixa liberada para quem ainda não apoia (a Abertura). */
    const val FAIXA_LIVRE = "00"
    const val PULO_MS = 15_000L
    val VELOCIDADES = listOf(0.75, 1.0, 1.25, 1.5, 1.75, 2.0)
    val MINUTOS_DO_TIMER = listOf(15, 30, 45, 60)

    /** Validade do manifest em memória: com áudio 6 h; sem áudio (404) 10 min. */
    const val VALIDADE_POSITIVA_MS = 6 * 60 * 60 * 1000L
    const val VALIDADE_NEGATIVA_MS = 10 * 60 * 1000L

    fun pastaDoCapitulo(chave: ChaveDoCapitulo, base: String = URL_BASE): String =
        "$base${chave.idioma.pasta}/main_${chave.era}/page_${chave.pagina}/"

    fun urlDoManifest(chave: ChaveDoCapitulo, base: String = URL_BASE): String = pastaDoCapitulo(chave, base) + "manifest.json"

    /** Resolve o "file" do manifest em relação à pasta do capítulo (aceita "01.ogg" e caminhos relativos com "../"). */
    fun resolver(pasta: String, arquivo: String): String {
        if (arquivo.contains("://")) return arquivo
        val esquema = pasta.substringBefore("://", "")
        val semEsquema = if (esquema.isEmpty()) pasta else pasta.substringAfter("://")
        val partes = semEsquema.trimEnd('/').split('/').toMutableList()
        for (parte in arquivo.split('/')) {
            when (parte) {
                "", "." -> Unit
                ".." -> if (partes.size > 1) partes.removeAt(partes.lastIndex)
                else -> partes += parte
            }
        }
        val caminho = partes.joinToString("/")
        return if (esquema.isEmpty()) caminho else "$esquema://$caminho"
    }
}

/** Identifica um capítulo: idioma do conteúdo, era e página. Id no formato do Android: "pt/1/1". */
data class ChaveDoCapitulo(val idioma: Idioma, val era: Int, val pagina: Int) {
    val id: String get() = "${idioma.pasta}/$era/$pagina"

    /** mediaId de uma faixa no player: "pt/1/1#03". */
    fun idDaMidia(faixa: String): String = "$id#$faixa"

    companion object {
        fun ler(id: String?): ChaveDoCapitulo? {
            val partes = id?.substringBefore('#')?.split("/") ?: return null
            if (partes.size != 3) return null
            val idioma = Idioma.entries.firstOrNull { it.pasta == partes[0] } ?: return null
            return ChaveDoCapitulo(idioma, partes[1].toIntOrNull() ?: return null, partes[2].toIntOrNull() ?: return null)
        }

        fun faixaDaMidia(idDaMidia: String?): String? = idDaMidia?.substringAfter('#', "")?.takeIf { it.isNotEmpty() }
    }
}

/** Início de um segmento dentro da faixa: [segundos]; [indiceDoBloco] = item do capítulo (-1 = abertura). */
data class MarcaDoAudio(val segmento: String, val segundos: Double, val indiceDoBloco: Int) {
    val inicioMs: Long get() = (segundos * 1000).toLong()
}

data class FaixaDoAudio(
    val id: String,
    val titulo: String,
    val tipo: String?,
    /** Arquivo original (.ogg, usado pelo Android). */
    val arquivo: String,
    /** Cópia AAC para o iOS (campo file_m4a do manifest); sem ele, o .ogg com a extensão trocada. */
    val arquivoM4a: String,
    val duracaoS: Double,
    val bytes: Long,
    val marcas: List<MarcaDoAudio>,
) {
    val livre: Boolean get() = id == ConfigDoAudio.FAIXA_LIVRE
    val duracaoMs: Long get() = (duracaoS * 1000).toLong()
}

/** Posição dentro do áudio do capítulo: faixa e milissegundos. */
data class PosicaoNoAudio(val faixa: Int, val ms: Long)

/**
 * manifest.json da narração de um capítulo (mesmo formato do Android, gerado por content-src/audio/generate.py).
 * As regras de sincronia texto↔áudio (UC-19) e "Ouvir a partir daqui" são as do Android.
 */
class ManifestDoAudio(
    val chave: ChaveDoCapitulo,
    val titulo: String,
    val tituloDaEra: String?,
    val faixas: List<FaixaDoAudio>,
    val duracaoS: Double,
    /** Pasta do capítulo (URL), usada para resolver o arquivo de cada faixa. */
    val pasta: String,
    /** JSON original, guardado quando o capítulo é baixado para ouvir offline. */
    val original: String,
) {
    val minutos: Int get() = max(1, (duracaoS / 60.0).roundToInt())
    val bytesTotais: Long get() = faixas.sumOf { it.bytes }

    fun urlDaFaixa(faixa: FaixaDoAudio): String = ConfigDoAudio.resolver(pasta, faixa.arquivoM4a)

    fun indiceDaFaixa(id: String?): Int = faixas.indexOfFirst { it.id == id }

    /** Bloco do capítulo lido na faixa [faixa] na posição [posicaoMs]; -1 = nenhum (abertura ou faixa inexistente). */
    fun blocoEm(faixa: Int, posicaoMs: Long): Int {
        val marcas = faixas.getOrNull(faixa)?.marcas ?: return -1
        if (marcas.isEmpty()) return -1
        val marca = marcas.lastOrNull { it.inicioMs <= posicaoMs + 150 } ?: marcas.first()
        return marca.indiceDoBloco.takeIf { it >= 0 } ?: -1
    }

    /** Onde começa a leitura do bloco [indiceDoBloco] (ou do próximo bloco narrado); null se não houver. */
    fun localizar(indiceDoBloco: Int): PosicaoNoAudio? {
        var melhor: Triple<Int, Int, Long>? = null
        faixas.forEachIndexed { f, faixa ->
            faixa.marcas.forEach { m ->
                if (m.indiceDoBloco == indiceDoBloco) return PosicaoNoAudio(f, m.inicioMs)
                if (m.indiceDoBloco > indiceDoBloco && (melhor == null || m.indiceDoBloco < melhor!!.first)) {
                    melhor = Triple(m.indiceDoBloco, f, m.inicioMs)
                }
            }
        }
        return melhor?.let { PosicaoNoAudio(it.second, it.third) }
    }

    /** Blocos com narração (para o "Ouvir a partir daqui"). */
    val blocosNarrados: Set<Int> by lazy { faixas.flatMap { f -> f.marcas.map { it.indiceDoBloco } }.filter { it >= 0 }.toSet() }

    companion object {
        /** Lê o manifest; lança exceção (Error no Swift) se o JSON for inválido ou não houver faixas, como o Android. */
        @Throws(Exception::class)
        fun ler(json: String, chave: ChaveDoCapitulo, pasta: String): ManifestDoAudio {
            val o = lerJson(json) as JsonObject
            val faixas = o.lista("tracks").map { t ->
                val id = requireNotNull(t.texto("id")) { "faixa sem id" }
                val arquivo = t.texto("file") ?: "$id.ogg"
                FaixaDoAudio(
                    id = id,
                    titulo = t.texto("title") ?: id,
                    tipo = t.texto("kind")?.takeIf { it.isNotEmpty() && it != "null" },
                    arquivo = arquivo,
                    arquivoM4a = t.texto("file_m4a") ?: arquivo.substringBeforeLast('.') + ".m4a",
                    duracaoS = t.decimal("duration_s") ?: 0.0,
                    bytes = t.longo("bytes") ?: 0,
                    marcas = t.lista("marks").map { m -> MarcaDoAudio(m.texto("seg").orEmpty(), m.decimal("t") ?: 0.0, m.inteiro("source_index") ?: -1) }
                        .sortedBy { it.segundos },
                )
            }
            require(faixas.isNotEmpty()) { "manifest sem faixas" }
            return ManifestDoAudio(
                chave = chave,
                titulo = o.texto("title").orEmpty(),
                tituloDaEra = o.texto("era_title")?.takeIf { it.isNotEmpty() && it != "null" },
                faixas = faixas,
                duracaoS = o.decimal("duration_s") ?: faixas.sumOf { it.duracaoS },
                pasta = pasta,
                original = json,
            )
        }
    }
}
