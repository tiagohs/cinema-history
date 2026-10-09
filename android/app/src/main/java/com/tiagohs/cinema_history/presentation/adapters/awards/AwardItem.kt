package com.tiagohs.cinema_history.presentation.adapters.awards

import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.contents.ContentBlockSpecial
import com.tiagohs.entities.contents.ContentImage
import com.tiagohs.entities.contents.ContentNominee
import com.tiagohs.entities.contents.ContentPersonList
import com.tiagohs.entities.contents.ContentText
import com.tiagohs.entities.contents.ContentVideo
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.entities.tmdb.person.Person

/**
 * Itens das listas da tela de prêmios (aba do ano e aba "Sobre").
 * [key] é estável dentro do mesmo ano/aba e alimenta o DiffUtil.
 */
sealed class AwardItem(val key: String, val viewType: Int) {

    class Info(val award: AwardMainTopic) : AwardItem("info", TYPE_INFO)

    /** Uma categoria: o(s) vencedor(es) primeiro, depois os indicados. */
    class Category(
        key: String,
        val name: String?,
        val nominees: List<Nominee>
    ) : AwardItem(key, TYPE_CATEGORY)

    class Section(key: String, val title: String) : AwardItem(key, TYPE_SECTION)

    /** Texto compacto em cartão (com "Ler mais") ou parágrafo simples. */
    class Text(
        key: String,
        val title: String?,
        val html: String?,
        val collapsible: Boolean,
        val card: Boolean,
        val image: Image? = null,
        val credits: String? = null,
        val click: Click? = null
    ) : AwardItem(key, if (card) TYPE_TEXT_CARD else TYPE_TEXT_PLAIN)

    class Videos(key: String, val title: String, val videos: List<ContentVideo>) : AwardItem(key, TYPE_VIDEO_ROW)

    class Video(key: String, val video: ContentVideo) : AwardItem(key, TYPE_VIDEO_WIDE)

    class People(key: String, val title: String?, val persons: List<Person>) : AwardItem(key, TYPE_PEOPLE_ROW)

    /** Imagem larga do filme vencedor, entre as categorias (quando o ano não tem vídeo para o lugar). */
    class Backdrop(key: String, val category: String?, val winner: Nominee, val movie: Nominee) :
        AwardItem(key, TYPE_BACKDROP)

    class ReadMore(val expanded: Boolean) : AwardItem("read_more", TYPE_READ_MORE)

    companion object {
        const val TYPE_INFO = 0
        const val TYPE_CATEGORY = 1
        const val TYPE_SECTION = 2
        const val TYPE_TEXT_CARD = 3
        const val TYPE_TEXT_PLAIN = 4
        const val TYPE_VIDEO_ROW = 5
        const val TYPE_VIDEO_WIDE = 6
        const val TYPE_PEOPLE_ROW = 7
        const val TYPE_READ_MORE = 8
        const val TYPE_BACKDROP = 9

        /** A cada quantas categorias entra um vídeo ou uma imagem. */
        private const val MEDIA_EVERY = 2

        /** Vencedores primeiro (mantendo a ordem original entre eles e entre os indicados). */
        fun sortWinnersFirst(list: List<Nominee>?): List<Nominee> {
            list ?: return emptyList()
            if (list.firstOrNull()?.winner == true && list.drop(1).none { it.winner == true }) return list
            return list.filter { it.winner == true } + list.filter { it.winner != true }
        }

        /**
         * Monta a aba do ano: categorias primeiro (são o destaque), texto da cerimônia compacto logo
         * após a primeira categoria, e no fim vídeos, júri e curiosidades.
         */
        fun forYear(
            year: String,
            content: List<Content>,
            ceremonyTitle: String,
            videosTitle: String,
            highlightsTitle: String
        ): List<AwardItem> {
            val categories = mutableListOf<Category>()
            val texts = mutableListOf<String>()
            val videos = mutableListOf<ContentVideo>()
            val people = mutableListOf<ContentPersonList>()
            val specials = mutableListOf<ContentBlockSpecial>()

            content.forEachIndexed { index, item ->
                when (item) {
                    is ContentNominee -> categories += Category(
                        "y$year:c$index",
                        item.name,
                        sortWinnersFirst(item.nomineeList)
                    )
                    is ContentText -> item.contentText?.takeIf { it.isNotBlank() }?.let { texts += it }
                    is ContentVideo -> videos += item
                    is ContentPersonList -> if (!item.persons.isNullOrEmpty()) people += item
                    is ContentBlockSpecial -> specials += item
                    else -> {}
                }
            }

            val items = ArrayList<AwardItem>(categories.size + 8)
            categories.firstOrNull()?.let { items += it }
            if (texts.isNotEmpty()) {
                items += Text(
                    "y$year:ceremony",
                    ceremonyTitle,
                    texts.joinToString("<br/><br/>"),
                    collapsible = true,
                    card = true
                )
            }
            // A cada duas categorias: um vídeo do ano ou, sem vídeos sobrando, a imagem de um vencedor.
            val pendingVideos = ArrayDeque(videos)
            var sinceMedia = 1
            categories.drop(1).forEachIndexed { index, category ->
                items += category
                sinceMedia++
                val isLast = index == categories.size - 2
                if (sinceMedia >= MEDIA_EVERY && !isLast) {
                    val media: AwardItem? = pendingVideos.removeFirstOrNull()
                        ?.let { Video("y$year:v${items.size}", it) }
                        ?: backdropOf(year, items.size, category)
                    if (media != null) {
                        items += media
                        sinceMedia = 0
                    }
                }
            }
            if (pendingVideos.isNotEmpty()) items += Videos("y$year:videos", videosTitle, pendingVideos.toList())
            people.forEachIndexed { index, list ->
                items += People("y$year:people$index", list.title, list.persons.orEmpty())
            }
            if (specials.isNotEmpty()) {
                items += Section("y$year:highlights", highlightsTitle)
                specials.forEachIndexed { index, special ->
                    items += Text(
                        "y$year:special$index",
                        special.title,
                        special.description,
                        collapsible = true,
                        card = true,
                        image = special.image,
                        credits = special.credits,
                        click = special.click
                    )
                }
            }
            return items
        }

        private fun backdropOf(year: String, position: Int, category: Category): Backdrop? {
            val winner = category.nominees.firstOrNull { it.winner == true } ?: return null
            val movie = if (winner.movie != null) winner.movie else winner
            if (movie?.backdropPath.isNullOrBlank()) return null
            return Backdrop("y$year:b$position", category.name, winner, movie!!)
        }

        /** Aba "Sobre": ficha do prêmio + o começo do histórico; o resto aparece em "Ler mais". */
        fun forHistory(award: AwardMainTopic, history: List<Content>, expanded: Boolean): List<AwardItem> {
            val all = ArrayList<AwardItem>(history.size + 2)
            all += Info(award)

            val body = history.mapIndexedNotNull { index, item -> historyItem(index, item) }
            val compactBody = compact(body)
            val needsToggle = compactBody.size < body.size

            all += if (expanded || !needsToggle) body else compactBody
            if (needsToggle) all += ReadMore(expanded)
            return all
        }

        /** Primeiros parágrafos (até [COMPACT_BLOCKS] textos), sem vídeos/imagens no meio. */
        private fun compact(body: List<AwardItem>): List<AwardItem> {
            val result = mutableListOf<AwardItem>()
            for (item in body) {
                if (item is Text && !item.card) result += item
                if (result.size >= COMPACT_BLOCKS) break
            }
            return result.ifEmpty { body.take(COMPACT_BLOCKS) }
        }

        private fun historyItem(index: Int, item: Content): AwardItem? = when (item) {
            is ContentText -> item.contentText?.let {
                Text("h$index", item.contentTitle, it, collapsible = false, card = false, credits = item.contentCredits)
            }
            is ContentVideo -> Video("h$index", item)
            is ContentImage -> Text(
                "h$index",
                item.information.contentTitle,
                item.information.contentText,
                collapsible = false,
                card = true,
                image = item.image,
                credits = item.information.source
            )
            is ContentBlockSpecial -> Text(
                "h$index",
                item.title,
                item.description,
                collapsible = false,
                card = true,
                image = item.image,
                credits = item.credits,
                click = item.click
            )
            is ContentPersonList -> People("h$index", item.title, item.persons.orEmpty())
            else -> null
        }

        private const val COMPACT_BLOCKS = 2
    }
}
