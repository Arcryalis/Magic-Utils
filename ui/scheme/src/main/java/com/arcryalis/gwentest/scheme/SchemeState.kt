package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls

sealed interface SchemeState{
    data object Loading: SchemeState

    data class Ready(
        val cards: List<CardUiInfo>,
        val ongoingCards: List<CardUiInfo>,
        val faceUpCards: List<CardUiInfo>,
        val overlayCard: CardUiInfo?
    ): SchemeState
}

data class CardUiInfo(
    val name: String,
    val oracleText: String?,
    val images: CardUiImageUrls,
    val isOngoing: Boolean,
)

data class CardUiImageUrls(
    val small: String,
    val large: String,
    val back: String
)