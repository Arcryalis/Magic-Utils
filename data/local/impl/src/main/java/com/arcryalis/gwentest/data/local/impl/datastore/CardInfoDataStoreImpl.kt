package com.arcryalis.gwentest.data.local.impl.datastore

import com.arcryalis.gwentest.data.local.api.CardInfoDataStore
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.impl.dao.CardDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardInfoDataStoreImpl @Inject constructor(
    private val cardDao: CardDao
): CardInfoDataStore {

    override suspend fun insertCards(cards: List<CardInfoEntity>) = cardDao.insertCards(cards)

    override fun getCardSet(setId: String): Flow<List<CardInfoEntity>> = cardDao.getCardSet(setId)
}