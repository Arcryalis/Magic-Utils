package com.arcryalis.gwentest.home.impl

import kotlinx.coroutines.flow.Flow

interface CardDataStore {

    suspend fun updateSet(data: List<CardInfo>)

    fun getSetExists(setId: String): Flow<Boolean>

    fun getSet(setId: String): Flow<List<CardInfo>>

}