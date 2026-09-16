package com.arcryalis.gwentest.domain.home

import com.arcryalis.gwentest.data.card.model.CardInfo
import kotlinx.coroutines.flow.Flow

interface GetCardInfoListUseCase {
    operator fun invoke(setId: String): Flow<List<CardInfo>>
}