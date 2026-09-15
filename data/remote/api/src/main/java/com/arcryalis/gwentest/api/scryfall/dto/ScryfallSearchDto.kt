package com.arcryalis.gwentest.api.scryfall.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@InternalSerializationApi
@Serializable
data class ScryfallSearchDto(
    @SerialName("has_more")
    val hasMore: Boolean? = null,
    val nextPage: String? = null,
    val data: List<ScryfallDataDto>
)

@InternalSerializationApi
@Serializable
data class ScryfallDataDto(
    val id: String,
    val name: String,
    @SerialName("image_uris")
    val imageUris: ScryfallImageUrlsDto,
    @SerialName("oracle_text")
    val oracleText: String,
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
    val normal: String,
    val large: String
)
