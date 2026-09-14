package com.arcryalis.gwentest.api.scryfall

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallSearchDto
import kotlinx.serialization.InternalSerializationApi

interface ScryfallDataSource {
    @OptIn(InternalSerializationApi::class)
    suspend fun getCards(query: String): RemoteResponse<ScryfallSearchDto>
}