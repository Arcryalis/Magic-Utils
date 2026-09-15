package com.arcryalis.gwentest.api.scryfall

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallSearchDto
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
interface ScryfallDataSource {
    suspend fun getCards(query: String): RemoteResponse<ScryfallSearchDto>

    suspend fun getCardsViaUrl(url :String): RemoteResponse<ScryfallSearchDto>
}