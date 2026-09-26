package com.arcryalis.gwentest.domain.card

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockAreCardsAvailableUseCase(
    private val result: Boolean = true
) : AreCardsAvailableUseCase {
    override fun invoke(): Flow<Boolean> = flowOf(result)
}
