package com.arcryalis.gwentest.data.local.api

import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockCardSetDataStore(
    private var localCardSets: MutableList<CardSetEntity>
) : CardSetDataStore {

    override suspend fun insertSets(cardSets: List<CardSetEntity>) {
        //don't return result
        localCardSets.addAll(cardSets)
    }

    override fun getSets(): Flow<List<CardSetEntity>> = flowOf(localCardSets)
}