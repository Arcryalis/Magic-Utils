package com.arcryalis.gwentest.api.scryfall

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallPaginationDto

class MockScryfallDataSource(
    private var responses: MutableMap<Int, RemoteResponse<ScryfallPaginationDto>> = mutableMapOf()
) : ScryfallDataSource {

    override suspend fun getSchemes(page: Int): RemoteResponse<ScryfallPaginationDto> =
        responses.getOrDefault(
            page,
            RemoteResponse.Error("No response configured for page $page")
        )
}