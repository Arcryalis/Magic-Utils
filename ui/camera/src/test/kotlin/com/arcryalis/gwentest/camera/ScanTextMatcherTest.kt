package com.arcryalis.gwentest.camera

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScanTextMatcherTest {

    private lateinit var sut: ScanTextMatcher

    private fun setupSut() {
        sut = ScanTextMatcher()
    }

    private fun feed(vararg texts: String): List<String?> = texts.map { sut.onText(it) }

    @Test
    fun whenSameTextThreeTimes_thenMatch() {
        setupSut()

        val results = feed("Geralt", "Geralt", "Geralt")

        assertEquals(listOf(null, null, "Geralt"), results)
    }

    @Test
    fun whenWhitespaceDiffers_thenNormalisedAndMatched() {
        setupSut()

        val results = feed("Geralt  of Rivia", " Geralt of Rivia ", "Geralt\nof Rivia")

        assertEquals("Geralt of Rivia", results.last())
    }

    @Test
    fun whenResultsDiffer_thenNoMatch() {
        setupSut()

        val results = feed("Geralt", "Yennefer", "Ciri")

        assertEquals(listOf(null, null, null), results)
    }

    @Test
    fun whenBlankResults_thenIgnored() {
        setupSut()

        val results = feed(" ", "\n", "")

        assertEquals(listOf(null, null, null), results)
    }

    @Test
    fun whenShortResults_thenIgnored() {
        setupSut()

        val results = feed("G", "Ge", "Ge", "Ge")

        assertEquals(listOf(null, null, null, null), results)
    }

    @Test
    fun whenStrippedResultTooShort_thenIgnored() {
        setupSut()

        val results = feed("Ox 12", "Ox 12", "Ox 12")

        assertNull(results.last())
    }

    @Test
    fun whenEndsWithSpaceAndNumber_thenNumberStripped() {
        setupSut()

        val results = feed("Geralt of Rivia 12", "Geralt of Rivia 12", "Geralt of Rivia 12")

        assertEquals("Geralt of Rivia", results.last())
    }

    @Test
    fun whenEndsWithNumberWithoutSpace_thenKept() {
        setupSut()

        val results = feed("Card42", "Card42", "Card42")

        assertEquals("Card42", results.last())
    }

    @Test
    fun whenTrailingNumbersDiffer_thenNoMatch() {
        setupSut()

        val results = feed("Geralt 12", "Geralt 13", "Geralt 14")

        assertNull(results.last())
    }

    @Test
    fun whenMatchesWithinWindow_thenMatch() {
        setupSut()

        val results = feed("Geralt", "Geralt", "Ciri", "Geralt")

        assertEquals(listOf(null, null, null, "Geralt"), results)
    }

    @Test
    fun whenMatchesFallOutsideWindow_thenNoMatch() {
        setupSut()

        val results = feed("Geralt", "Ciri", "Yennefer", "Triss", "Geralt", "Dandelion", "Geralt")

        assertNull(results.last())
    }

    @Test
    fun whenReset_thenHistoryCleared() {
        setupSut()

        feed("Geralt", "Geralt")
        sut.reset()
        val result = sut.onText("Geralt")

        assertNull(result)
    }

    @Test
    fun whenMatched_thenHistoryCleared() {
        setupSut()

        feed("Geralt", "Geralt", "Geralt")
        val results = feed("Geralt", "Geralt")

        assertEquals(listOf(null, null), results)
    }
}
