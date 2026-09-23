package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import kotlinx.coroutines.flow.Flow

interface GetShuffledCardInfoUseCase {
    operator fun invoke(setId: String): Flow<List<CardInfo>>
}