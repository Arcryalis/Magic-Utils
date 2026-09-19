package com.arcryalis.gwentest.api.scryfall

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallPaginationDto
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
interface ScryfallDataSource {
    suspend fun getSchemes(page: Int): RemoteResponse<ScryfallPaginationDto>
}