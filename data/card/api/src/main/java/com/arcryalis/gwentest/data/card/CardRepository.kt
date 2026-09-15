package com.arcryalis.gwentest.data.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardSet
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun downloadSchemes(): Boolean

    fun getAvailableSets(): Flow<List<CardSet>>

    fun getSet(setId: String): Flow<List<CardInfo>>

}