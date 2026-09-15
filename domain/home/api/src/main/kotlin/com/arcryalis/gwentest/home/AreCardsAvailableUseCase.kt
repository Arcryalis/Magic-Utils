package com.arcryalis.gwentest.home

interface AreCardsAvailableUseCase {
    suspend operator fun invoke(): Boolean
}