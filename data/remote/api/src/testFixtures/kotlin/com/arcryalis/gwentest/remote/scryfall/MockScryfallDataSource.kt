package com.arcryalis.gwentest.remote.scryfall

import com.arcryalis.gwentest.remote.RemoteResponse
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallCatalogDto
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallNamedCardDto
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallPaginationDto

class MockScryfallDataSource(
    private var responses: MutableMap<Int, RemoteResponse<ScryfallPaginationDto>> = mutableMapOf(),
    var autocompleteResponse: RemoteResponse<ScryfallCatalogDto> = RemoteResponse.Success(ScryfallCatalogDto(emptyList())),
    var namedCardResponse: RemoteResponse<ScryfallNamedCardDto> = RemoteResponse.Error("No named card response configured")
) : ScryfallDataSource {

    var lastAutocompleteQuery: String? = null
        private set

    var lastNamedCardQuery: String? = null
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

    override suspend fun getCardByExactName(name: String): RemoteResponse<ScryfallNamedCardDto> {
        lastNamedCardQuery = name
        return namedCardResponse
    }
}