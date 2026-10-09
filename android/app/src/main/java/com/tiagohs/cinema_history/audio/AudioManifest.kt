package com.tiagohs.cinema_history.audio

import org.json.JSONObject
import kotlin.math.max
import kotlin.math.roundToInt

/** Início de um segmento dentro da faixa: [t] em segundos; [sourceIndex] = item do content_list (-1 = abertura). */
data class AudioMark(val seg: String, val t: Double, val sourceIndex: Int) {
    val startMs: Long get() = (t * 1000).toLong()
}

data class AudioTrack(
    val id: String,
    val title: String,
    val kind: String?,
    val file: String,
    val durationS: Double,
    val bytes: Long,
    val marks: List<AudioMark>
) {
    val isFree: Boolean get() = id == AudioConfig.FREE_TRACK_ID
    val durationMs: Long get() = (durationS * 1000).toLong()
}

/**
 * manifest.json de um capítulo (formato do content-src/audio/generate.py → generate_chapter).
 * Lido com org.json (sem reflexão, seguro com R8).
 */
data class AudioManifest(
    val key: ChapterKey,
    val title: String,
    val eraTitle: String?,
    val tracks: List<AudioTrack>,
    val durationS: Double,
    /** Pasta do capítulo (URL ou asset:///), usada para resolver o "file" de cada faixa. */
    val dir: String,
    /** JSON original, guardado em disco quando o capítulo é baixado para ouvir offline. */
    val raw: String
) {

    val minutes: Int get() = max(1, (durationS / 60.0).roundToInt())

    val totalBytes: Long get() = tracks.sumOf { it.bytes }

    fun trackUri(track: AudioTrack): String = AudioConfig.resolve(dir, track.file)

    fun indexOf(trackId: String?): Int = tracks.indexOfFirst { it.id == trackId }

    /** Item do content_list que está sendo lido na faixa [trackIndex], na posição [positionMs]. */
    fun sourceIndexAt(trackIndex: Int, positionMs: Long): Int? {
        val marks = tracks.getOrNull(trackIndex)?.marks ?: return null
        if (marks.isEmpty()) return null
        val mark = marks.lastOrNull { it.startMs <= positionMs + 150 } ?: marks.first()
        return mark.sourceIndex.takeIf { it >= 0 }
    }

    /** Onde começa a leitura do item [sourceIndex] (ou do próximo item narrado): faixa + posição em ms. */
    fun locate(sourceIndex: Int): Pair<Int, Long>? {
        var best: Triple<Int, Int, Long>? = null // (sourceIndex encontrado, faixa, ms)
        tracks.forEachIndexed { ti, track ->
            track.marks.forEach { m ->
                if (m.sourceIndex == sourceIndex) return ti to m.startMs
                if (m.sourceIndex > sourceIndex && (best == null || m.sourceIndex < best!!.first)) {
                    best = Triple(m.sourceIndex, ti, m.startMs)
                }
            }
        }
        return best?.let { it.second to it.third }
    }

    /** Os itens do content_list que têm narração (para o "Ouvir a partir daqui"). */
    val narratedIndexes: Set<Int> by lazy {
        tracks.flatMap { t -> t.marks.map { it.sourceIndex } }.filter { it >= 0 }.toSet()
    }

    companion object {

        fun parse(json: String, key: ChapterKey, dir: String): AudioManifest {
            val o = JSONObject(json)
            val arr = o.getJSONArray("tracks")
            val tracks = (0 until arr.length()).map { i ->
                val t = arr.getJSONObject(i)
                val marksArr = t.optJSONArray("marks")
                val marks = if (marksArr == null) emptyList() else (0 until marksArr.length()).map { j ->
                    val m = marksArr.getJSONObject(j)
                    AudioMark(m.optString("seg"), m.optDouble("t", 0.0), m.optInt("source_index", -1))
                }
                AudioTrack(
                    id = t.getString("id"),
                    title = t.optString("title", t.getString("id")),
                    kind = t.optString("kind").takeIf { it.isNotEmpty() && it != "null" },
                    file = t.optString("file", t.getString("id") + ".ogg"),
                    durationS = t.optDouble("duration_s", 0.0),
                    bytes = t.optLong("bytes", 0L),
                    marks = marks.sortedBy { it.t }
                )
            }
            require(tracks.isNotEmpty()) { "manifest sem faixas" }
            return AudioManifest(
                key = key,
                title = o.optString("title"),
                eraTitle = o.optString("era_title").takeIf { it.isNotEmpty() && it != "null" },
                tracks = tracks,
                durationS = o.optDouble("duration_s", tracks.sumOf { it.durationS }),
                dir = dir,
                raw = json
            )
        }
    }
}
