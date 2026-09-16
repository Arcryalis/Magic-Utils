package com.arcryalis.gwentest.data.card.model

data class CardInfo(
    val name: String,
    val oracleText: String?,
    val images: CardInfoImageUrls,
    val isOngoing: Boolean
)

data class CardInfoImageUrls(
    val small: String,
    val large: String,
    val back: String
)