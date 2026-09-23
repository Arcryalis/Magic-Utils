package com.arcryalis.gwentest.remote.scryfall

import com.arcryalis.gwentest.remote.RemoteResponse
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallPaginationDto
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
interface ScryfallDataSource {
    suspend fun getSchemes(page: Int): RemoteResponse<ScryfallPaginationDto>
}