package com.arcryalis.gwentest.game

import androidx.compose.ui.graphics.Color
import com.arcryalis.gwentest.data.card.model.CardInfo


sealed interface GameState {
    data object Loading: GameState

    data class Ready(
        val players: List<PlayerInfo>?,
        val deckSettings: DeckSettings?,
        val overlayState: OverlayState,
    ): GameState
}

sealed interface OverlayState {
    data object Hidden: OverlayState

    sealed interface Visible: OverlayState {
        data class Individual(
            val info: CardInfo,
            val showRemove: Boolean
        ): Visible

        data class Gallery(
            val cards: List<CardInfo>
        ): Visible
    }
}

data class PlayerInfo(
    val name: String,
    val lifeTotal: Int,
    val backgroundColor: Color = Color.LightGray
)

data class DeckSettings(
    val nextCardUrl: String?,
    val revealedCards: List<CardInfo>?,
    val extraCardList: List<CardInfo>
)