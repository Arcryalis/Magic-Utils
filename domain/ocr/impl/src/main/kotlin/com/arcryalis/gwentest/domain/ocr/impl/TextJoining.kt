package com.arcryalis.gwentest.domain.ocr.impl

internal const val MIN_LINE_CONFIDENCE = 0.5f

private val WHITESPACE = Regex("\\s+")

internal data class RecognisedLine(val text: String, val confidence: Float)

internal fun joinLines(
    lines: List<RecognisedLine>,
    minConfidence: Float = MIN_LINE_CONFIDENCE,
): String = lines
    .filter { it.confidence >= minConfidence }
    .joinToString(" ") { it.text }
    .replace(WHITESPACE, " ")
    .trim()
