package com.arcryalis.gwentest.domain.card

class MockGetCardBackUrlUseCase(
    private val cardBackUrl: String
) : GetCardBackUrlUseCase {
    override suspend fun invoke(): String = cardBackUrl
}
