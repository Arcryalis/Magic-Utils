package com.arcryalis.gwentest.remote.impl

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.RemoteResponse.Error
import com.arcryalis.gwentest.api.RemoteResponse.Success
import com.arcryalis.gwentest.api.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallPaginationDto
import com.arcryalis.gwentest.remote.impl.api.ScryfallApi
import com.haroldadmin.cnradapter.NetworkResponse
import javax.inject.Inject

class ScryfallDataSourceImpl @Inject constructor(
    private val scryfallApi: ScryfallApi
): ScryfallDataSource {

    override suspend fun getSchemes(page: Int): RemoteResponse<ScryfallPaginationDto> {//= handleResponse(
        // request duplicates in case of reprints
        // order by set to prevent multiple partial sets error
        // includeExtras defaults true on page 1 but false on 2+
        val response = scryfallApi.getAllSchemes(
            query = "t=scheme",
            unique = "prints",
            order = "set",
            includeExtras = true,
            page = page
        )

        return handleResponse(response)
    }

    override suspend fun getNextPage(url: String): RemoteResponse<ScryfallPaginationDto> {
        val response = scryfallApi.getNextPage(url)
        return handleResponse(
            response
        )
    }

    private fun <T> handleResponse(response: NetworkResponse<T, Unit>): RemoteResponse<T> = when (response) {
        is NetworkResponse.Success -> Success(response.body)
        is NetworkResponse.Error -> Error()
    }
}