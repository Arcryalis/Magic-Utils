package com.arcryalis.gwentest.data.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardSet
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun doCardsExistLocally(): Flow<Boolean>

    suspend fun downloadSchemes(): Boolean

    fun getAvailableSets(): Flow<List<CardSet>>

    fun getSet(setId: String): Flow<List<CardInfo>>

    fun getAllCards(): Flow<List<CardInfo>>

    fun getCardBackUrl(): String

    suspend fun autocompleteCardNames(query: String): Result<List<String>>

    suspend fun getCardmarketUrl(cardName: String): Result<String>

}