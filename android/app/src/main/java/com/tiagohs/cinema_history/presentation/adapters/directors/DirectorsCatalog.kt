package com.tiagohs.cinema_history.presentation.adapters.directors

import androidx.annotation.StringRes
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.main_topics.DirectorsMainTopic

/** Épocas da tela de diretores (campo "era" do directorsmaintopics.json), em ordem cronológica. */
enum class DirectorEra(val key: String, @StringRes val title: Int, @StringRes val period: Int) {
    SILENT("silent", R.string.directors_era_silent, R.string.directors_era_silent_period),
    GOLDEN("golden", R.string.directors_era_golden, R.string.directors_era_golden_period),
    NEW_WAVES("new_waves", R.string.directors_era_new_waves, R.string.directors_era_new_waves_period),
    MODERN("modern", R.string.directors_era_modern, R.string.directors_era_modern_period),
    CONTEMPORARY("contemporary", R.string.directors_era_contemporary, R.string.directors_era_contemporary_period);

    companion object {
        fun from(key: String?): DirectorEra? = values().firstOrNull { it.key == key }
    }
}

/** Regiões (campo "region" do directorsmaintopics.json). */
enum class DirectorRegion(val key: String, @StringRes val label: Int) {
    EUROPE("europe", R.string.directors_region_europe),
    NORTH_AMERICA("north_america", R.string.directors_region_north_america),
    LATIN_AMERICA("latin_america", R.string.directors_region_latin_america),
    ASIA("asia", R.string.directors_region_asia),
    AFRICA("africa", R.string.directors_region_africa),
    OCEANIA("oceania", R.string.directors_region_oceania);

    companion object {
        fun from(key: String?): DirectorRegion? = values().firstOrNull { it.key == key }
    }
}

/** Linhas da lista de diretores. */
sealed class DirectorsRow {
    abstract val id: String

    data class Intro(val total: Int) : DirectorsRow() {
        override val id = "intro"
    }

    /** Cabeçalho de seção: uma época, ou a seção "Em alta" quando [era] é nulo. */
    data class Section(val era: DirectorEra?, val count: Int) : DirectorsRow() {
        override val id = "section_${era?.key ?: "trending"}"
    }

    data class Trending(val directors: List<DirectorsMainTopic>) : DirectorsRow() {
        override val id = "trending_carousel"
    }

    data class Card(val director: DirectorsMainTopic, val showRank: Boolean) : DirectorsRow() {
        override val id = "card_${director.personId}"
    }

    object Empty : DirectorsRow() {
        override val id = "empty"
    }
}

/**
 * Monta a lista a partir dos filtros.
 *
 *  - [FILTER_ALL]: carrossel "Em alta" no topo + todas as épocas, cada uma com seu cabeçalho;
 *  - [FILTER_TRENDING]: só os diretores em alta, na ordem do ranking;
 *  - chave de época: só aquela época.
 * O filtro de região vale para todos os casos (null = todas).
 */
object DirectorsCatalog {

    const val FILTER_ALL = "all"
    const val FILTER_TRENDING = "trending"

    fun rows(all: List<DirectorsMainTopic>, filter: String, region: String?): List<DirectorsRow> {
        val inRegion = all.filter { region == null || it.region == region }
        val rows = mutableListOf<DirectorsRow>()

        when (filter) {
            FILTER_TRENDING -> {
                val trending = trending(inRegion)
                if (trending.isNotEmpty()) {
                    rows += DirectorsRow.Section(null, trending.size)
                    rows += trending.map { DirectorsRow.Card(it, showRank = true) }
                }
            }
            FILTER_ALL -> {
                if (region == null) rows += DirectorsRow.Intro(all.size)

                val trending = trending(inRegion)
                if (trending.isNotEmpty()) {
                    rows += DirectorsRow.Section(null, trending.size)
                    rows += DirectorsRow.Trending(trending)
                }
                DirectorEra.values().forEach { era -> rows += eraRows(inRegion, era) }
            }
            else -> DirectorEra.from(filter)?.let { era -> rows += eraRows(inRegion, era) }
        }

        if (rows.none { it is DirectorsRow.Card || it is DirectorsRow.Trending }) {
            return listOf(DirectorsRow.Empty)
        }
        return rows
    }

    /** Índice logo depois do carrossel "Em alta" (onde entra o anúncio nativo), ou -1. */
    fun adPosition(rows: List<DirectorsRow>): Int {
        val index = rows.indexOfFirst { it is DirectorsRow.Trending }
        return if (index >= 0) index + 1 else -1
    }

    private fun trending(list: List<DirectorsMainTopic>) =
        list.filter { it.trending > 0 }.sortedBy { it.trending }

    private fun eraRows(list: List<DirectorsMainTopic>, era: DirectorEra): List<DirectorsRow> {
        val directors = list.filter { DirectorEra.from(it.era) == era }
        if (directors.isEmpty()) return emptyList()

        return listOf(DirectorsRow.Section(era, directors.size)) +
            directors.map { DirectorsRow.Card(it, showRank = false) }
    }
}
