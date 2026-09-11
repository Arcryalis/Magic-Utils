package com.arcryalis.gwentest.home.impl

import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun downloadSet(id: String)

    fun isSetAvailable(id: String): Flow<Boolean>

    fun getSet(setId: String): Flow<List<CardInfo>>

}