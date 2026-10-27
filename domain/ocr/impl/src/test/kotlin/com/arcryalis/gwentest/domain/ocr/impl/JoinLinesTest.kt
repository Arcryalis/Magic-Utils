package com.arcryalis.gwentest.domain.ocr.impl

import org.junit.Test
import kotlin.test.assertEquals

class JoinLinesTest {

    @Test
    fun whenLowConfidenceLine_thenDropped() {
        val lines = listOf(
            RecognisedLine("Geralt", 0.9f),
            RecognisedLine("noise", 0.2f),
        )

        assertEquals("Geralt", joinLines(lines, minConfidence = 0.5f))
    }

    @Test
    fun whenLineAtThreshold_thenKept() {
        val lines = listOf(RecognisedLine("Geralt", 0.5f))

        assertEquals("Geralt", joinLines(lines, minConfidence = 0.5f))
    }

    @Test
    fun whenExtraWhitespace_thenCollapsed() {
        val lines = listOf(
            RecognisedLine("  Geralt ", 0.9f),
            RecognisedLine("of\tRivia  ", 0.9f),
        )

        assertEquals("Geralt of Rivia", joinLines(lines, minConfidence = 0.5f))
    }

    @Test
    fun whenNoLines_thenEmpty() {
        assertEquals("", joinLines(emptyList(), minConfidence = 0.5f))
    }
}
