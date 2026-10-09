package com.tiagohs.cinema_history.awards

import com.tiagohs.cinema_history.presentation.adapters.awards.AwardItem
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.contents.ContentNominee
import com.tiagohs.entities.contents.ContentText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AwardItemTest {

    private fun nominee(name: String, winner: Boolean) = Nominee(name = name, winner = winner)

    @Test
    fun winnersComeFirstKeepingOrder() {
        val list = listOf(nominee("a", false), nominee("b", true), nominee("c", false), nominee("d", true))
        assertEquals(listOf("b", "d", "a", "c"), AwardItem.sortWinnersFirst(list).map { it.name })
    }

    @Test
    fun yearPutsCategoriesFirstAndCeremonyTextAfterFirstCategory() {
        val content = listOf(
            ContentText(contentText = "texto", font = null),
            ContentNominee(name = "Melhor Filme", nomineeList = listOf(nominee("x", true))),
            ContentNominee(name = "Melhor Direção", nomineeList = listOf(nominee("y", false)))
        )
        val items = AwardItem.forYear("2026", content, "Cerimônia", "Vídeos", "Destaques")

        assertTrue(items[0] is AwardItem.Category)
        assertTrue(items[1] is AwardItem.Text)
        assertTrue(items[2] is AwardItem.Category)
        assertEquals(3, items.size)
        assertEquals(items.size, items.map { it.key }.toSet().size)
    }
}
