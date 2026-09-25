package com.arcryalis.gwentest.home

import com.arcryalis.gwentest.data.card.model.CardSet

sealed interface HomeState {

    data object Error: HomeState

    data object Loading: HomeState

    data class Ready(
        val playerCount: HomeItemState<Int?>,
        val startingLife: HomeItemState<Int>,
        val sets: HomeItemState<CardSet?>,
    ): HomeState
}

data class HomeItemState<T>(
    val items: List<T>,
    val selectedItem: T
)