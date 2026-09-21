package com.arcryalis.gwentest.data.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockCardRepository(
    private val availableSets: List<CardSet>,
    private val cardsBySet: Map<String, List<CardInfo>>,
    private var cardsExistLocally: Boolean,
    private var downloadResult: Boolean,
) : CardRepository {

    override suspend fun doCardsExistLocally(): Boolean = cardsExistLocally

    override suspend fun downloadSchemes(): Boolean = downloadResult

    override fun getAvailableSets(): Flow<List<CardSet>> = flowOf(availableSets)

    override fun getSet(setId: String): Flow<List<CardInfo>> =
        flowOf(cardsBySet.getOrDefault(setId, emptyList()))
}