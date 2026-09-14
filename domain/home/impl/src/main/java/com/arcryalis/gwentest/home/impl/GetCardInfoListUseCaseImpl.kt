package com.arcryalis.gwentest.home.impl

import com.arcryalis.gwentest.data.card.CardInfo
import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.home.GetCardInfoListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardInfoListUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): GetCardInfoListUseCase {
    override operator fun invoke(): Flow<List<CardInfo>> = cardRepo.getSet("oe01")
}