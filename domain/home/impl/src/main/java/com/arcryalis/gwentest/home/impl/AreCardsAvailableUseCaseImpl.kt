package com.arcryalis.gwentest.home.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.home.AreCardsAvailableUseCase
import javax.inject.Inject

class AreCardsAvailableUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
): AreCardsAvailableUseCase {
    override suspend fun invoke(): Boolean = cardRepository.doCardsExistLocally()
}