package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockGetCardSetsUseCase(
    private val cardSets: List<CardSet> = emptyList()
) : GetCardSetsUseCase {
    override fun invoke(): Flow<List<CardSet>> = flowOf(cardSets)
}
