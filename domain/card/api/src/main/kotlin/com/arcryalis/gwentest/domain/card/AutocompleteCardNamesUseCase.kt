package com.arcryalis.gwentest.domain.card

interface AutocompleteCardNamesUseCase {
    suspend operator fun invoke(query: String): Result<List<String>>
}
