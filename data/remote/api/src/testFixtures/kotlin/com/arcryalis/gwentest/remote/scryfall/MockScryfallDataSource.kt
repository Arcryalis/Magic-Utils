package com.arcryalis.gwentest.remote.scryfall

import com.arcryalis.gwentest.remote.RemoteResponse
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallPaginationDto

class MockScryfallDataSource(
    private var responses: MutableMap<Int, RemoteResponse<ScryfallPaginationDto>> = mutableMapOf()
) : ScryfallDataSource {

    override suspend fun getSchemes(page: Int): RemoteResponse<ScryfallPaginationDto> =
        responses.getOrDefault(
            page,
            RemoteResponse.Error("No response configured for page $page")
        )
}