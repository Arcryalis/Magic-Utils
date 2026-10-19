package com.arcryalis.gwentest.data.local.impl.datastore

import com.arcryalis.gwentest.data.local.api.CardInfoDataStore
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.impl.dao.CardInfoDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardInfoDataStoreImpl @Inject constructor(
    private val cardInfoDao: CardInfoDao
): CardInfoDataStore {

    override fun doCardsExistLocally(): Flow<Boolean> = cardInfoDao.doCardsExistLocally()

    override suspend fun insertCards(cards: List<CardInfoEntity>) = cardInfoDao.insertCards(cards)

    override fun getCardSet(setId: String): Flow<List<CardInfoEntity>> = cardInfoDao.getCardSet(setId)

    override fun getAllCards(): Flow<List<CardInfoEntity>> = cardInfoDao.getAllCards()
}