package com.arcryalis.gwentest.domain.home.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.home.GetCardSetsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardSetsUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): GetCardSetsUseCase {
    override fun invoke(): Flow<List<CardSet>> = cardRepo.getAvailableSets()
}