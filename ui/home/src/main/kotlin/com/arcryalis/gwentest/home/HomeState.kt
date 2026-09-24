package com.arcryalis.gwentest.home

import com.arcryalis.gwentest.data.card.model.CardSet

sealed interface HomeState {

    data object Error: HomeState

    data object Loading: HomeState

    data class Ready(
        val availableSets: List<CardSet>,
        val selectedSet: CardSet?
    ): HomeState
}