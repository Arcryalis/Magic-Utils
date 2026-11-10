package com.arcryalis.gwentest.domain.card

import kotlinx.coroutines.CompletableDeferred

class MockGetCardmarketUrlUseCase(
    results: List<Result<String>> = emptyList(),
    private val gate: CompletableDeferred<Unit>? = null
) : GetCardmarketUrlUseCase {
    private val results = ArrayDeque(results)

    var invocationCount = 0
        private set

    var lastCardName: String? = null
        private set

    override suspend fun invoke(cardName: String): Result<String> {
        invocationCount++
        lastCardName = cardName
        gate?.await()
        return results.removeFirstOrNull() ?: Result.failure(IllegalStateException())
    }
}
