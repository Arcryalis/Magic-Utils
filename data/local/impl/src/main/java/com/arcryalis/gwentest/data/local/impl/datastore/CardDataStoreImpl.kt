package com.arcryalis.gwentest.data.local.impl.datastore

import com.arcryalis.gwentest.data.local.api.CardDataStore
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.impl.dao.CardDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardDataStoreImpl @Inject constructor(
    private val cardDao: CardDao
): CardDataStore {

    override suspend fun insertCards(cards: List<CardInfoEntity>) = cardDao.insertCards(cards)

    override fun getSetExists(setId: String): Flow<Boolean> = cardDao.getSetExists(setId)

    override fun getSet(setId: String): Flow<List<CardInfoEntity>> = cardDao.getSet(setId)

}