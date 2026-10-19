package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.data.card.model.CardInfo

sealed interface SchemeState{
    data object Loading: SchemeState
    data class Ready(
        val cards: List<CardInfo>,
        val overlayCard: CardInfo?
    ): SchemeState
}