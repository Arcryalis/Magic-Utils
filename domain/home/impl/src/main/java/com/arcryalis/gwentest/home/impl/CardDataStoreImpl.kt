package com.arcryalis.gwentest.home.impl

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardDataStoreImpl @Inject constructor(
    private val cardDao: CardDao
): CardDataStore {

    override suspend fun updateSet(data: List<CardInfo>) = cardDao.updateSet(data)

    override fun getSetExists(setId: String): Flow<Boolean> = cardDao.getSetExists(setId)

    override fun getSet(setId: String): Flow<List<CardInfo>> = cardDao.getSet(setId)

}