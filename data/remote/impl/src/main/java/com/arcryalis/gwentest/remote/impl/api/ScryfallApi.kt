package com.arcryalis.gwentest.remote.impl.api

import com.arcryalis.gwentest.api.scryfall.dto.ScryfallCardDto
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET

interface ScryfallApi {

    @GET("TODO")
    suspend fun getCards(setId: String): NetworkResponse<List<ScryfallCardDto>, Unit>

}