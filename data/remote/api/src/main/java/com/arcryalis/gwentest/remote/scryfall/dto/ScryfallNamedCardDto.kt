package com.arcryalis.gwentest.remote.scryfall.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScryfallNamedCardDto(
    @SerialName("purchase_uris")
    val purchaseUris: ScryfallPurchaseUrisDto? = null
)

@Serializable
data class ScryfallPurchaseUrisDto(
    val cardmarket: String? = null
)
