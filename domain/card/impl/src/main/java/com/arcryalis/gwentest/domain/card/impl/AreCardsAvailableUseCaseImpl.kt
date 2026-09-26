package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.domain.card.AreCardsAvailableUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AreCardsAvailableUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
): AreCardsAvailableUseCase {
    override fun invoke(): Flow<Boolean> = cardRepository.doCardsExistLocally()
}
