package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockGetAllCardCardInfoUseCase(
    private val cardInfo: List<CardInfo>
): GetAllCardCardInfoUseCase {

    override fun invoke(): Flow<List<CardInfo>> = flowOf(cardInfo)
}
