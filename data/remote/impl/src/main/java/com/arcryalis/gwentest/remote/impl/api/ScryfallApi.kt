package com.arcryalis.gwentest.remote.impl.api

import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallCatalogDto
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallPaginationDto
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

    @GET("cards/autocomplete")
    suspend fun autocompleteCardNames(
        @Query("q") query: String
    ): NetworkResponse<ScryfallCatalogDto, Unit>
}