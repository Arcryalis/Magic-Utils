package com.arcryalis.gwentest.data.local.api

import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import kotlinx.coroutines.flow.Flow

interface CardSetDataStore {

    suspend fun insertSets(cardSets: List<CardSetEntity>)

    fun getSets(): Flow<List<CardSetEntity>>

}