package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockGetShuffledCardInfoUseCase: GetShuffledCardInfoUseCase {
    override fun invoke(setId: String): Flow<List<CardInfo>> = flowOf(
        listOf(
            MockCardInfo.cardInfo
        )
    )
}