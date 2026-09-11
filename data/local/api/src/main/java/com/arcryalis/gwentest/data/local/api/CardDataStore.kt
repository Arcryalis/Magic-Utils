package com.arcryalis.gwentest.data.local.api

import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import kotlinx.coroutines.flow.Flow

interface CardDataStore {

    suspend fun insertCards(cards: List<CardInfoEntity>)

    fun getSetExists(setId: String): Flow<Boolean>

    fun getSet(setId: String): Flow<List<CardInfoEntity>>

}