package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.random.Random

class GetShuffledCardInfoUseCaseImpl @Inject constructor(
    private val cardRepo: CardRepository
): GetShuffledCardInfoUseCase {
    override operator fun invoke(setId: String): Flow<List<CardInfo>> {
        // Seed fixed per invocation so database re-emissions don't reorder the deck mid-game
        val seed = Random.nextLong()
        return cardRepo.getSet(setId).map { it.shuffled(Random(seed)) }
    }
}