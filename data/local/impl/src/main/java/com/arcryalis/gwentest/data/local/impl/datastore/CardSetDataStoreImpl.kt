package com.arcryalis.gwentest.data.local.impl.datastore

import com.arcryalis.gwentest.data.local.api.CardSetDataStore
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import com.arcryalis.gwentest.data.local.impl.dao.CardSetDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardSetDataStoreImpl @Inject constructor(
    private val cardSetDao: CardSetDao
): CardSetDataStore {
    override suspend fun insertSets(cardSets: List<CardSetEntity>) = cardSetDao.insertCardSets(cardSets)

    override fun getSets(): Flow<List<CardSetEntity>> = cardSetDao.getSets()

}