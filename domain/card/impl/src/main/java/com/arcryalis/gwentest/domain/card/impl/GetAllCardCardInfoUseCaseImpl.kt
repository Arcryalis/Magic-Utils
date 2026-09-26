package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetAllCardCardInfoUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCardCardInfoUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): GetAllCardCardInfoUseCase {
    override operator fun invoke(): Flow<List<CardInfo>> = cardRepo.getAllCards()
}
