package com.arcryalis.gwentest.remote.scryfall.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScryfallCatalogDto(
    @SerialName("data")
    val data: List<String>
)
