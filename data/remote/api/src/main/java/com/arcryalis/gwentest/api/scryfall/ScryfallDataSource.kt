package com.arcryalis.gwentest.api.scryfall

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallCardDto

interface ScryfallDataSource {
    suspend fun getCards(query: String): RemoteResponse<List<ScryfallCardDto>>
}