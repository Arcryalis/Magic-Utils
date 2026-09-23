package com.arcryalis.gwentest.domain.card

class MockAreCardsAvailableUseCase(
    private val result: Boolean = true
) : AreCardsAvailableUseCase {
    override suspend fun invoke(): Boolean = result
}
