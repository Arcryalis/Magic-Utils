package com.arcryalis.gwentest.api.scryfall.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@InternalSerializationApi
@Serializable
data class ScryfallSearchPageDto(
    @SerialName("has_more")
    val hasMore: Boolean,
    @SerialName("next_page")
    val nextPage: String? = null,
    val data: List<ScryfallDataPageDto>
)

@InternalSerializationApi
@Serializable
data class ScryfallDataPageDto(
    val id: String,
    val name: String,
    @SerialName("type_line")
    val type: String,
    @SerialName("set")
    val setId: String,
    @SerialName("set_name")
    val setName: String,
)