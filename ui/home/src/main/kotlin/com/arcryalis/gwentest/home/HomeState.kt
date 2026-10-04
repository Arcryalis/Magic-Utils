package com.arcryalis.gwentest.home

import com.arcryalis.gwentest.data.card.model.CardSet

sealed interface HomeState {
    data object Initial: HomeState

    data class Ready(
        val players: List<String>,
        val startingLife: HomeItemState<Int>,
        val sets: HomeItemState<CardSet?>,
        val isRefreshing: Boolean
    ): HomeState
}

data class HomeItemState<T>(
    val items: List<T>,
    val selectedItem: T,
    val buttonState: HomeItemButtonState = HomeItemButtonState.NoButton
)

interface HomeItemButtonState {

    data object NoButton: HomeItemButtonState

    data object NotLoaded: HomeItemButtonState

    data object Loading: HomeItemButtonState

    data object Error: HomeItemButtonState

    data object Available: HomeItemButtonState
}
