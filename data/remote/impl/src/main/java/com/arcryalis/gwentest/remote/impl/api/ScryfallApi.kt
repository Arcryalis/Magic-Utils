package com.arcryalis.gwentest.remote.impl.api

import com.arcryalis.gwentest.api.scryfall.dto.ScryfallSearchDto
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ScryfallApi {

    @GET("cards/search")
    suspend fun getCards(
        @Query("q") query: String,
//        @Query("unique") unique: String = "cards",
//        @Query("order") order: String = "name",
    ): NetworkResponse<ScryfallSearchDto, Unit>

}