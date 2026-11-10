package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.card.GetCardSetsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardSetsUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): GetCardSetsUseCase {
    override fun invoke(): Flow<List<CardSet>> = cardRepo.getAvailableSets()
}