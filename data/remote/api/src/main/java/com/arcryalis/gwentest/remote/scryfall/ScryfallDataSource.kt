package com.arcryalis.gwentest.remote.scryfall

import com.arcryalis.gwentest.remote.RemoteResponse
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallPaginationDto

interface ScryfallDataSource {
    suspend fun getSchemes(page: Int): RemoteResponse<ScryfallPaginationDto>
}
