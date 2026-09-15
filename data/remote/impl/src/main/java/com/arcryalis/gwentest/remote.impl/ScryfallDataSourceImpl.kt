package com.arcryalis.gwentest.remote.impl

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.RemoteResponse.Error
import com.arcryalis.gwentest.api.RemoteResponse.Success
import com.arcryalis.gwentest.api.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallSearchDto
import com.arcryalis.gwentest.remote.impl.api.ScryfallApi
import com.haroldadmin.cnradapter.NetworkResponse
import javax.inject.Inject

class ScryfallDataSourceImpl @Inject constructor(
    private val scryfallApi: ScryfallApi
): ScryfallDataSource {

    override suspend fun getCards(query: String): RemoteResponse<ScryfallSearchDto> = handleResponse(
        scryfallApi.getCards(query)
    )

    override suspend fun getCardsViaUrl(url: String): RemoteResponse<ScryfallSearchDto> = handleResponse(
        scryfallApi.getCardsViaUrl(url)
    )

    private fun handleResponse(response: NetworkResponse<ScryfallSearchDto, Unit>): RemoteResponse<ScryfallSearchDto> = when (response) {
        is NetworkResponse.Success -> Success(response.body)
        is NetworkResponse.Error -> Error()
    }
}