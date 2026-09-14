package com.arcryalis.gwentest.remote.impl

import android.util.Log
import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.RemoteResponse.*
import com.arcryalis.gwentest.api.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallSearchDto
import com.arcryalis.gwentest.remote.impl.api.ScryfallApi
import com.haroldadmin.cnradapter.NetworkResponse
import javax.inject.Inject

class ScryfallDataSourceImpl @Inject constructor(
    private val scryfallApi: ScryfallApi
): ScryfallDataSource {
    companion object {
        const val LOG_TAG = "ScryfallDataSourceImpl"
    }

    override suspend fun getCards(query: String): RemoteResponse<ScryfallSearchDto> {
        val response = scryfallApi.getCards(query)
        Log.i(LOG_TAG, "Response $response")

        return when (response) {
            is NetworkResponse.Success -> Success(response.body)
            is NetworkResponse.Error -> Error()
        }
    }

}