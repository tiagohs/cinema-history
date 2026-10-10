package com.tiagohs.cinema_history.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AudioManifestTest {

    private val key = ChapterKey("pt", 1, 1)

    private fun manifest() = AudioManifest(
        key = key,
        title = "Capítulo",
        eraTitle = null,
        tracks = listOf(
            AudioTrack("00", "Abertura", "open", "00.ogg", 10.0, 100, listOf(AudioMark("00-01", 0.25, -1), AudioMark("00-02", 5.0, -1))),
            AudioTrack("01", "Parte 1", "body", "01.ogg", 30.0, 100, listOf(AudioMark("01-01", 0.25, 0), AudioMark("01-02", 12.0, 1))),
            AudioTrack("02", "Parte 2", "body", "02.ogg", 30.0, 100, listOf(AudioMark("02-01", 0.25, 3), AudioMark("02-02", 20.0, 5)))
        ),
        durationS = 70.0,
        dir = "https://audio.example.invalid/cinema-history/pt/main_1/page_1/",
        raw = "{}"
    )

    @Test
    fun sourceIndexAtFollowsMarks() {
        val m = manifest()
        assertNull(m.sourceIndexAt(0, 6_000)) // abertura: sem parágrafo
        assertEquals(0, m.sourceIndexAt(1, 0))
        assertEquals(0, m.sourceIndexAt(1, 11_000))
        assertEquals(1, m.sourceIndexAt(1, 12_000))
        assertEquals(5, m.sourceIndexAt(2, 29_000))
    }

    @Test
    fun locateFindsSegmentOrNextNarratedItem() {
        val m = manifest()
        assertEquals(1 to 12_000L, m.locate(1))
        assertEquals(2 to 250L, m.locate(2)) // item 2 não é narrado (imagem): começa no próximo (3)
        assertEquals(2 to 20_000L, m.locate(5))
        assertNull(m.locate(9))
    }

    @Test
    fun resolveTrackUris() {
        val m = manifest()
        assertEquals("https://audio.example.invalid/cinema-history/pt/main_1/page_1/01.ogg", m.trackUri(m.tracks[1]))
        val test = AudioConfig.resolve("asset:///audio-test/pt/main_1/page_1/", "../../../tones/b.ogg")
        assert(test.endsWith("/audio-test/tones/b.ogg")) { test }
    }

    @Test
    fun mediaIds() {
        val id = AudioIds.mediaId(key, "03")
        assertEquals("pt/1/1#03", id)
        assertEquals(key, AudioIds.chapterOf(id))
        assertEquals("03", AudioIds.trackOf(id))
    }
}
