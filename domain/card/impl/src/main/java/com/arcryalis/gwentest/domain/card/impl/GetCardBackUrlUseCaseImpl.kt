package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.domain.card.GetCardBackUrlUseCase
import javax.inject.Inject

class GetCardBackUrlUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
) : GetCardBackUrlUseCase {
    override suspend operator fun invoke(): String = cardRepo.getCardBackUrl()
}
