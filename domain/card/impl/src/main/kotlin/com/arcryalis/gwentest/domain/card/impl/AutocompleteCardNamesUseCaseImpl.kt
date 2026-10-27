package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.domain.card.AutocompleteCardNamesUseCase
import javax.inject.Inject

class AutocompleteCardNamesUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
) : AutocompleteCardNamesUseCase {
    override suspend fun invoke(query: String): Result<List<String>> =
        cardRepository.autocompleteCardNames(query.trim())
}
