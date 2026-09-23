package com.arcryalis.gwentest.remote.scryfall.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class ScryfallPaginationDto(
    @SerialName("has_more")
    val hasMore: Boolean,
    val warnings: String? = null,
    val data: List<ScryfallDataDto>
)

@InternalSerializationApi
@Serializable
data class ScryfallDataDto(
    val id: String,
    val name: String,
    @SerialName("image_uris")
    val imageUris: ScryfallImageUrlsDto? = null,
    @SerialName("oracle_text")
    val oracleText: String? = null,
    @SerialName("type_line")
    val type: String,
    @SerialName("set")
    val setId: String,
    @SerialName("set_name")
    val setName: String
)

@InternalSerializationApi
@Serializable
data class ScryfallImageUrlsDto(
    val small: String,
    val large: String
)
