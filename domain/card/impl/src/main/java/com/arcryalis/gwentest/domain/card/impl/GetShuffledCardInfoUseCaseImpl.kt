package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetShuffledCardInfoUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): GetShuffledCardInfoUseCase {
    override operator fun invoke(setId: String): Flow<List<CardInfo>> = cardRepo.getSet(setId).map {
        it.shuffled()
    }
}