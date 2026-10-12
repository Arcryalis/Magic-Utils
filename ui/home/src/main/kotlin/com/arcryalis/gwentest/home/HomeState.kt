package com.arcryalis.gwentest.home

import androidx.compose.ui.graphics.Color
import com.arcryalis.gwentest.data.card.model.CardSet

sealed interface HomeState {
    data object Initial: HomeState

    data class Ready(
        val players: List<HomePlayerInfo>,
        val playerColorOptions: List<Color>,
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

data class HomePlayerInfo(
    val name: String = "",
    val color: Color = Color.Unspecified
)

interface HomeItemButtonState {

    data object NoButton: HomeItemButtonState

    data object NotLoaded: HomeItemButtonState

    data object Loading: HomeItemButtonState

    data object Error: HomeItemButtonState

    data object Available: HomeItemButtonState
}
