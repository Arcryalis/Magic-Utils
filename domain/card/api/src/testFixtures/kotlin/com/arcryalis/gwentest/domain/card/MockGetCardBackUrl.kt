package com.arcryalis.gwentest.domain.card

class MockGetCardBackUrl(
    private val cardBackUrl: String
) : GetCardBackUrl {
    override suspend fun invoke(): String = cardBackUrl
}
