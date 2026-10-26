package com.arcryalis.gwentest.remote.scryfall

import com.arcryalis.gwentest.remote.RemoteResponse
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallCatalogDto
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallPaginationDto

class MockScryfallDataSource(
    private var responses: MutableMap<Int, RemoteResponse<ScryfallPaginationDto>> = mutableMapOf(),
    var autocompleteResponse: RemoteResponse<ScryfallCatalogDto> = RemoteResponse.Success(ScryfallCatalogDto(emptyList()))
) : ScryfallDataSource {

    var lastAutocompleteQuery: String? = null
        private set

    override suspend fun getSchemes(page: Int): RemoteResponse<ScryfallPaginationDto> =
        responses.getOrDefault(
            page,
            RemoteResponse.Error("No response configured for page $page")
        )

    override suspend fun autocompleteCardNames(query: String): RemoteResponse<ScryfallCatalogDto> {
        lastAutocompleteQuery = query
        return autocompleteResponse
    }
}