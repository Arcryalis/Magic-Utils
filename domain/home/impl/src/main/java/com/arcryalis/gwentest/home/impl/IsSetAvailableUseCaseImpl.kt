package com.arcryalis.gwentest.home.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.home.IsSetAvailableUseCase
import com.arcryalis.gwentest.home.TestUseCase
import javax.inject.Inject

class IsSetAvailableUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): IsSetAvailableUseCase {
    override suspend fun invoke(): String {
        val temp = cardRepo.getSet("TEST")
        return "Android"
    }
}