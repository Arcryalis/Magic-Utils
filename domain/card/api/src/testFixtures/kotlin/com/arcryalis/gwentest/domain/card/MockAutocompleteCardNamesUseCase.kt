package com.arcryalis.gwentest.domain.card

import kotlinx.coroutines.CompletableDeferred

class MockAutocompleteCardNamesUseCase(
    results: List<Result<List<String>>> = emptyList(),
    private val gate: CompletableDeferred<Unit>? = null
) : AutocompleteCardNamesUseCase {
    private val results = ArrayDeque(results)

    var invocationCount = 0
        private set

    var lastQuery: String? = null
        private set

    override suspend fun invoke(query: String): Result<List<String>> {
        invocationCount++
        lastQuery = query
        gate?.await()
        return results.removeFirstOrNull() ?: Result.success(emptyList())
    }
}
