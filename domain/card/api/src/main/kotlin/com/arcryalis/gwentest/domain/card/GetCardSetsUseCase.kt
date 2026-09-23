package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardSet
import kotlinx.coroutines.flow.Flow

interface GetCardSetsUseCase {
    operator fun invoke(): Flow<List<CardSet>>
}