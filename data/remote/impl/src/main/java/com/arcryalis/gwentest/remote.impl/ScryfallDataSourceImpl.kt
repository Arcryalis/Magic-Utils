package com.arcryalis.gwentest.remote.impl

import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallCardDto
import com.arcryalis.gwentest.remote.impl.api.ScryfallApi
import com.haroldadmin.cnradapter.NetworkResponse
import javax.inject.Inject

class ScryfallDataSourceImpl @Inject constructor(
    private val scryfallApi: ScryfallApi
): ScryfallDataSource {
    override suspend fun getCards(setId: String): RemoteResponse<List<ScryfallCardDto>> {
        val response = scryfallApi.getCards(setId)

        val temp: RemoteResponse<List<ScryfallCardDto>> = when (response) {
            is NetworkResponse.Success -> RemoteResponse.Success(
                response.body
            )
            is NetworkResponse.Error -> RemoteResponse.Error(response.error?.message)
        }

        return temp
    }

}