package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls

sealed interface SchemeState{
    data object Loading: SchemeState

    data class Ready(
        val cards: List<CardUiInfo>,
        val ongoingCards: List<CardUiInfo>
    ): SchemeState
}

data class CardUiInfo(
    val name: String,
    val images: CardInfoImageUrls,
    val isOngoing: Boolean,
    val isFaceUp: Boolean
)