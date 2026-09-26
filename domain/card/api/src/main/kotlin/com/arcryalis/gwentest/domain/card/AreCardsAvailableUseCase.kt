package com.arcryalis.gwentest.domain.card

import kotlinx.coroutines.flow.Flow

interface AreCardsAvailableUseCase {
    operator fun invoke(): Flow<Boolean>
}
