package com.arcryalis.gwentest.home.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.home.TestUseCase
import javax.inject.Inject

class TestUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
): TestUseCase {
    override suspend fun invoke(): String = "Android"
}