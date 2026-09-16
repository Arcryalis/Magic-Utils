package com.arcryalis.gwentest.domain.home

interface AreCardsAvailableUseCase {
    suspend operator fun invoke(): Boolean
}