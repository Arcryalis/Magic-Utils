package com.arcryalis.gwentest.data.card

import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun downloadSet(setId: String)

    fun isSetAvailable(setId: String): Flow<Boolean>

    fun getSet(setId: String): Flow<List<CardInfo>>

}