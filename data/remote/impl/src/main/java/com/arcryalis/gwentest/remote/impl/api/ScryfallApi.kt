package com.arcryalis.gwentest.remote.impl.api

import com.arcryalis.gwentest.api.scryfall.dto.ScryfallPaginationDto
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ScryfallApi {

    @GET("cards/search")
    suspend fun getAllSchemes(
        @Query("q") query: String,
        @Query("unique") unique: String,
        @Query("order") order: String,
        @Query("include_extras") includeExtras: Boolean,
        @Query("page") page: Int
    ): NetworkResponse<ScryfallPaginationDto, Unit>
}