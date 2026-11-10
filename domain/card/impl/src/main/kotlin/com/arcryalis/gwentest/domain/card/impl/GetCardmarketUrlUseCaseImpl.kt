package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.domain.card.GetCardmarketUrlUseCase
import javax.inject.Inject

class GetCardmarketUrlUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
) : GetCardmarketUrlUseCase {
    override suspend fun invoke(cardName: String): Result<String> =
        cardRepository.getCardmarketUrl(cardName)
}
