package com.arcryalis.gwentest.camera

private const val RECENT_RESULTS_SIZE = 5
private const val REQUIRED_MATCHES = 3
private const val MIN_SCAN_LENGTH = 3

private val WHITESPACE = Regex("\\s+")
private val TRAILING_NUMBER = Regex(" \\d+$")

internal class ScanTextMatcher {

    private val recentResults = ArrayDeque<String>()

    fun onText(text: String): String? {
        val normalised = text.trim().replace(WHITESPACE, " ")
        val stripped = normalised.replace(TRAILING_NUMBER, "")
        if (stripped.length < MIN_SCAN_LENGTH) return null

        recentResults.addLast(normalised)
        if (recentResults.size > RECENT_RESULTS_SIZE) {
            recentResults.removeFirst()
        }

        if (recentResults.count { it == normalised } < REQUIRED_MATCHES) return null

        recentResults.clear()
        return stripped
    }

    fun reset() {
        recentResults.clear()
    }
}
