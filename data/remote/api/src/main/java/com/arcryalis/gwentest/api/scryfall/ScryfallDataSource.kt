package com.arcryalis.gwentest.api.scryfall

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallCardDto

interface ScryfallDataSource {
    suspend fun getCards(setId: String): RemoteResponse<List<ScryfallCardDto>>
}