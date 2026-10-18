package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetAllCardInfoUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCardInfoUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): GetAllCardInfoUseCase {
    override operator fun invoke(): Flow<List<CardInfo>> = cardRepo.getAllCards()
}
