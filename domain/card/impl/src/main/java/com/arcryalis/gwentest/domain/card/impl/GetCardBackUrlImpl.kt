package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.domain.card.GetCardBackUrl
import javax.inject.Inject

class GetCardBackUrlImpl @Inject constructor(
    private val cardRepo: CardRepository
) : GetCardBackUrl {
    override suspend operator fun invoke(): String = cardRepo.getCardBackUrl()
}
