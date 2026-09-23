package com.arcryalis.gwentest.domain.card

interface AreCardsAvailableUseCase {
    suspend operator fun invoke(): Boolean
}