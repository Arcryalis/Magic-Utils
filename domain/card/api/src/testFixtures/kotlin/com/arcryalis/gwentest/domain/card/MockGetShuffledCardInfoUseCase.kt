package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockGetShuffledCardInfoUseCase(
    private val cardInfo: Map<String, List<CardInfo>>
): GetShuffledCardInfoUseCase {

    override fun invoke(setId: String): Flow<List<CardInfo>> = flowOf(
        cardInfo.getOrDefault(setId, emptyList())
    )
}