package com.arcryalis.gwentest.data.local.api

import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import kotlinx.coroutines.flow.Flow

interface CardInfoDataStore {

    suspend fun insertCards(cards: List<CardInfoEntity>)

    fun getCardSet(setId: String): Flow<List<CardInfoEntity>>

}