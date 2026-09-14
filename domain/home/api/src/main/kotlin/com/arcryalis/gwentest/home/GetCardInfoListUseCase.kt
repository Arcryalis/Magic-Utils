package com.arcryalis.gwentest.home

import com.arcryalis.gwentest.data.card.CardInfo
import kotlinx.coroutines.flow.Flow

interface GetCardInfoListUseCase {
    operator fun invoke(): Flow<List<CardInfo>>
}