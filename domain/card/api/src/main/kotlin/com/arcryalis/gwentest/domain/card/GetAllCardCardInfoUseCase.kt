package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import kotlinx.coroutines.flow.Flow

interface GetAllCardCardInfoUseCase {
    operator fun invoke(): Flow<List<CardInfo>>
}
