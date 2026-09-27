package com.arcryalis.gwentest.data.local.api

import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockCardInfoDataStore(
    private var localCards: MutableMap<String, MutableList<CardInfoEntity>>
) : CardInfoDataStore {

    override fun doCardsExistLocally(): Flow<Boolean> = flowOf(localCards.isNotEmpty())

    override suspend fun insertCards(cards: List<CardInfoEntity>) {
        cards.groupBy { it.setId }
            .forEach { (setId, cardList) ->
                if (localCards.keys.contains(setId)) {
                    localCards[setId]?.addAll(cardList)
                } else {
                    localCards[setId] = cardList.toMutableList()
                }
        }
    }

    override fun getCardSet(setId: String): Flow<List<CardInfoEntity>> = flowOf(
        localCards.getOrDefault(setId, emptyList())
    )

    override fun getAllCards(): Flow<List<CardInfoEntity>> = flowOf(
        localCards.values.flatten()
    )
}
